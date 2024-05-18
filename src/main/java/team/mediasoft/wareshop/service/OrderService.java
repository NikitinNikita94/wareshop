package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.data.repository.CustomerRepository;
import team.mediasoft.wareshop.data.repository.OrderRepository;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.Customer;
import team.mediasoft.wareshop.entity.Order;
import team.mediasoft.wareshop.entity.OrderItem;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderDto;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderProductDtoInfo;
import team.mediasoft.wareshop.entity.dto.order.OrderDtoIfo;
import team.mediasoft.wareshop.entity.dto.order.OrderStatusDto;
import team.mediasoft.wareshop.entity.dto.order.ReadOrderDto;
import team.mediasoft.wareshop.entity.embeddable.OrderItemPk;
import team.mediasoft.wareshop.entity.enumeration.OrderStatus;
import team.mediasoft.wareshop.exception.CustomerNotFoundException;
import team.mediasoft.wareshop.exception.OrderNotFoundException;
import team.mediasoft.wareshop.exception.ProductAvailableException;
import team.mediasoft.wareshop.exception.ProductNotFoundException;
import team.mediasoft.wareshop.mapper.OrderMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Метод по созданию заказа.
     *
     * @param customerId     - айди клиента для нового заказа
     * @param createOrderDto - входящие данные по заказу
     */
    @Transactional
    public void createOrder(long customerId, CreateOrderDto createOrderDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Клиента с таким Id=" + customerId + " не существует."));

        Order newOrder = Order.builder()
                .deliveryAddress(createOrderDto.getDeliveryAddress())
                .customer(customer)
                .status(OrderStatus.CREATED)
                .build();

        Map<UUID, Product> productMap = getUuidProductMap(createOrderDto.getProducts());
        List<OrderItem> orderItems = createOrderDto.getProducts().stream()
                .map(orderItem -> createOrderItem(orderItem, productMap, newOrder))
                .toList();
        newOrder.setOrderItems(orderItems);
        orderRepository.save(newOrder);
        log.info("Успешно сохраненный заказ!");
    }

    /**
     * Метод добавляет новые товары к заказу или обновляет старые.
     *
     * @param customerId                - айди клиента
     * @param orderId                   - айди заказа
     * @param createOrderProductDtoInfo - данные о новом заказе
     */
    @Transactional
    public void updateOrder(long customerId, UUID orderId, List<CreateOrderProductDtoInfo> createOrderProductDtoInfo) {
        Order updateOrder = checkCustomerAndOrderStatusById(customerId, orderId);

        Map<UUID, Product> productMap = getUuidProductMap(createOrderProductDtoInfo);
        Map<UUID, OrderItem> orderProductMap = updateOrder.getOrderItems().stream()
                .collect(toMap(oi -> oi.getProduct().getId(), Function.identity()));

        createOrderProductDtoInfo.forEach(orderInfo -> {
            final UUID productId = orderInfo.productId();
            int orderQuantity = orderInfo.quantity();
            final Product product = productMap.get(productId);

            checkProductByException(product, productId, orderQuantity);
            Optional.ofNullable(orderProductMap.get(orderInfo.productId()))
                    .ifPresentOrElse(
                            item -> {
                                item.setQuantity(item.getQuantity() + orderQuantity);
                                item.setPrice(product.getPrice());
                            },
                            () -> {
                                final OrderItem newOrderItem = OrderItem.builder()
                                        .pk(new OrderItemPk(updateOrder.getId(), productId))
                                        .order(updateOrder)
                                        .product(product)
                                        .quantity(orderQuantity)
                                        .price(product.getPrice())
                                        .build();
                                updateOrder.getOrderItems().add(newOrderItem);
                            }
                    );
            product.setAmount(product.getAmount() - orderQuantity);
        });


        orderRepository.save(updateOrder);
        log.info("Заказ успешно обновлен.");
    }

    /**
     * Метод возвращает товары принадлежащие к одному заказу.
     *
     * @param customerId - айди клиента
     * @param orderId    - айди заказа
     * @return OrderDtoIfo - возвращает информацию по данному заказу.
     */
    public OrderDtoIfo findOrderById(long customerId, UUID orderId) {
        Order order = orderRepository.findByIdFetchOrderedProducts(orderId)
                .map(checkOrder -> {
                    if (checkOrder.getCustomer().getId() != customerId) {
                        throw new CustomerNotFoundException("Не подходящий id клиента.");
                    }
                    return checkOrder;
                })
                .orElseThrow();
        return orderMapper.toOrderDtoIfo(order);
    }

    /**
     * Метод делает мягкое удаление заказа,
     * возвращая количество товара на склад.
     *
     * @param customerId - айди клиента
     * @param orderId    - айди заказа
     */
    @Transactional
    public void delete(Long customerId, UUID orderId) {
        Order order = checkCustomerAndOrderStatusById(customerId, orderId);
        order.getOrderItems()
                .forEach(item -> {
                    item.getProduct().setAmount(item.getProduct().getAmount() + item.getQuantity());
                    item.getProduct().setIsAvailable(false);
                });
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order);
    }

    /**
     * Метод меняет статус заказа.
     *
     * @param dto     - входящие данные со статусом
     * @param orderId - айди заказа
     * @return ReadOrderDto - возвращает информацию по заказу
     */
    @Transactional
    public ReadOrderDto updateStatus(OrderStatusDto dto, UUID orderId) {
        return Optional.of(orderRepository.findById(orderId)
                        .map(order -> {
                            order.setStatus(dto.status());
                            return orderMapper.toReadOrderDto(order);
                        }))
                .get()
                .orElseThrow(() -> new OrderNotFoundException("Заказа с таким идентификатором нет=" + orderId));
    }

    /**
     * Вспомогательный метод возвращает Map c айди продукта и самим продуктом.
     *
     * @param createOrderDto - данные о продукте.
     * @return Map<UUID, Product>
     */
    private Map<UUID, Product> getUuidProductMap(List<CreateOrderProductDtoInfo> createOrderDto) {
        List<UUID> uuidProductList = createOrderDto.stream()
                .map(CreateOrderProductDtoInfo::productId)
                .toList();

        return productRepository.findByIdIn(uuidProductList)
                .stream()
                .collect(toMap(Product::getId, Function.identity()));
    }

    /**
     * Метод на проверку принадлежности клиента и статуса заказа.
     *
     * @param customerId - айди клиента.
     * @param orderId    - айди заказа.
     * @return Order - возвращает заказ.
     */
    private Order checkCustomerAndOrderStatusById(Long customerId, UUID orderId) {
        return orderRepository.findByIdFetchOrderedProducts(orderId)
                .map(order -> {
                    if (!order.getStatus().equals(OrderStatus.CREATED) || order.getCustomer().getId() != customerId.longValue()) {
                        throw new OrderNotFoundException("Неверный статус заказа или ID клиента.");
                    }
                    return order;
                })
                .orElseThrow();
    }

    /**
     * Метод по созданию объектов заказа.
     *
     * @param orderItem  - объекты заказа.
     * @param productMap - ассоциативный массив объектов и айди продукта.
     * @param newOrder   - новый заказ.
     * @return OrderItem
     */
    private OrderItem createOrderItem(CreateOrderProductDtoInfo orderItem, Map<UUID, Product> productMap, Order newOrder) {
        final UUID productId = orderItem.productId();
        int orderedQuantity = orderItem.quantity();
        final Product product = productMap.get(orderItem.productId());

        checkProductByException(product, productId, orderedQuantity);
        product.setAmount(product.getAmount() - orderedQuantity);

        return OrderItem.builder()
                .pk(new OrderItemPk(newOrder.getId(), productId))
                .product(product)
                .order(newOrder)
                .quantity(orderedQuantity)
                .price(product.getPrice())
                .build();
    }

    /**
     * Метод для проверки продукта.
     *
     * @param product         - продукт.
     * @param productId       - айди продукта.
     * @param orderedQuantity - количество товара.
     */
    private void checkProductByException(Product product, UUID productId, int orderedQuantity) {
        if (product == null) {
            throw new ProductNotFoundException("Товар с указанным id=" + productId + " не найден.");
        }
        if (product.getAmount() < orderedQuantity) {
            throw new OrderNotFoundException("Не достаточное количество товара на складе.");
        }
        if (!product.getIsAvailable()) {
            throw new ProductAvailableException("Продукт не доступен к заказу productId=" + productId);
        }
    }
}
