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
                .map(orderItemProduct -> {
                    Product product = productMap.get(orderItemProduct.productId());
                    return createOrderItem(orderItemProduct, product, newOrder);
                })
                .toList();
        newOrder.setOrderItems(orderItems);
        orderRepository.save(newOrder);
        log.info("Успешно сохраненный заказ!");
    }

    /**
     * Метод добавляет новые товары к заказу.
     *
     * @param customerId                - айди клиента
     * @param orderId                   - айди заказа
     * @param createOrderProductDtoInfo - данные о новом заказе
     */
    @Transactional
    public void updateOrder(long customerId, UUID orderId, List<CreateOrderProductDtoInfo> createOrderProductDtoInfo) {
        Order updateOrder = checkCustomerAndOrderStatusById(customerId, orderId);

        Map<UUID, Product> uuidProductMap = getUuidProductMap(createOrderProductDtoInfo);
        List<Product> addedListProductByOrder = updateOrder.getOrderItems().stream()
                .map(OrderItem::getProduct)
                .toList();

        createOrderProductDtoInfo.stream()
                .filter(newProduct -> !addedListProductByOrder.contains(uuidProductMap.get(newProduct.productId())))
                .forEach(orderItemProduct -> {
                    Product product = uuidProductMap.get(orderItemProduct.productId());
                    updateOrder.getOrderItems().add(createOrderItem(orderItemProduct, product, updateOrder));
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
     * Метод по созданию товаров заказа.
     *
     * @param dtoInfo  - информация о продукте.
     * @param product  - продукт.
     * @param newOrder - заказ.
     * @return OrderItem - товары одного заказа.
     */
    private static OrderItem createOrderItem(CreateOrderProductDtoInfo dtoInfo, Product product, Order newOrder) {
        if (product.getIsAvailable() && product.getAmount() >= dtoInfo.quantity()) {
            product.setAmount(product.getAmount() - dtoInfo.quantity());
            return new OrderItem(new OrderItemPk(newOrder.getId(), product.getId()),
                    newOrder, product, dtoInfo.quantity(), product.getPrice());
        } else {
            throw new ProductAvailableException("Продукт не доступен " + "getIsAvailable=" + product.getIsAvailable() +
                                                " или недостает количества " + "amount=" + product.getAmount());
        }
    }
}
