package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.data.repository.OrderItemRepository;
import team.mediasoft.wareshop.data.repository.OrderRepository;
import team.mediasoft.wareshop.data.repository.ProductRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Метод по созданию заказа.
     *
     * @param customerId     - айди клиента для нового заказа
     * @param createOrderDto - входящие данные по заказу
     * @return ReadOrderDto - возвращает новый заказ с данными.
     */
    @Transactional
    public ReadOrderDto createOrder(long customerId, CreateOrderDto createOrderDto) {
        checkProductException(createOrderDto.getProducts());
        reductionOfAmountWhenOrdering(createOrderDto.getProducts());

        Order saveOrder = orderRepository.save(orderMapper.toCreateOrder(createOrderDto, customerId));
        createOrderDto.getProducts().forEach(product ->
                addOrderItem(productRepository.getReferenceById(product.productId()), saveOrder, product.quantity()));

        return orderMapper.toReadOrderDto(saveOrder);
    }

    /**
     * Метод добавляет новые товары к заказу.
     *
     * @param customerId                - айди клиента
     * @param orderId                   - айди заказа
     * @param createOrderProductDtoInfo - данные о новом заказе
     * @return OrderDtoIfo - возвращает информацию по данному заказу.
     */
    @Transactional
    public OrderDtoIfo updateOrder(long customerId, UUID orderId, List<CreateOrderProductDtoInfo> createOrderProductDtoInfo) {
        checkProductException(createOrderProductDtoInfo);
        reductionOfAmountWhenOrdering(createOrderProductDtoInfo);
        Order order = checkCustomerByOrder(customerId, orderId);

        createOrderProductDtoInfo
                .forEach(product -> {
                    if (order.getStatus().equals(OrderStatus.CREATED)) {
                        addOrderItem(productRepository.getReferenceById(product.productId()), order, product.quantity());
                    }
                });

        return orderMapper.toOrderDtoIfo(order);
    }

    /**
     * Метод возвращает товары принадлежащие к одному заказу.
     *
     * @param customerId - айди клиента
     * @param orderId    - айди заказа
     * @return OrderDtoIfo - возвращает информацию по данному заказу.
     */
    public OrderDtoIfo findOrderById(long customerId, UUID orderId) {
        Order order = checkCustomerByOrder(customerId, orderId);
        return orderMapper.toOrderDtoIfo(order);
    }

    /**
     * Метод делает мягкое удаление заказа,
     * возвращая количество товара на склад.
     *
     * @param customerId - айди клиента
     * @param orderId    - айди заказа
     * @return true or false
     */
    @Transactional
    public boolean delete(Long customerId, UUID orderId) {
        Order order = checkCustomerByOrder(customerId, orderId);
        orderRepository.findByOrderItemByOrder(order)
                .forEach(item -> {
                    if (order.getStatus().equals(OrderStatus.CREATED)) {
                        productRepository.deleteByProductId(item.getPk().getProduct().getId());
                        productRepository.updateAmountProductPlus(item.getPk().getProduct().getId(), item.getQuantity());
                    }
                });

        return Optional.of(order).stream()
                .map(ord -> {
                    ord.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(ord);
                    return true;
                })
                .findAny().orElse(false);
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
                .orElseThrow(() -> new OrderNotFoundException("There is no order with this ID=" + orderId));
    }

    /**
     * Вспомогательный метод на проверку требований к продукту.
     *
     * @param createOrderDto - входные данные для проверки
     */
    private void checkProductException(List<CreateOrderProductDtoInfo> createOrderDto) {
        createOrderDto
                .forEach(product -> {
                    if (!productRepository.existsById(product.productId())) {
                        throw new ProductNotFoundException("Product with id " + product.productId() + " not found");
                    }
                    if (!productRepository.existsByAmountIsGreaterThanEqual(product.quantity())) {
                        throw new ProductNotFoundException("Product with id " + product.productId() + " and quantity=" + product.quantity() + " insufficient in stock");
                    }
                    if (!productRepository.getIsAvailableById(product.productId())) {
                        throw new ProductAvailableException("Product with id " + product.productId() + " is not available, false");
                    }
                });
    }

    /**
     * Вспомогательный метод на проверку количества товара на складе,
     * в случае удачи убирает заявленное количество товара со склада.
     *
     * @param createOrderDto - входные данные для проверки
     */
    private void reductionOfAmountWhenOrdering(List<CreateOrderProductDtoInfo> createOrderDto) {
        createOrderDto
                .forEach(product -> {
                    if (productRepository.existsByAmountIsLessThan(product.quantity())) {
                        throw new ProductAvailableException("Product with id " + product.productId() + " is already available, false");
                    }
                    productRepository.updateAmountProductMinus(product.productId(), product.quantity());
                });
    }

    /**
     * Метод проверяет принадлежность товара к заявленному клиенту.
     *
     * @param customerId - айди клиента
     * @param orderId    - айди заказа
     * @return Order - возвращает товар по заявленному айди
     */
    private Order checkCustomerByOrder(long customerId, UUID orderId) {
        return orderRepository.findById(orderId).stream()
                .map(order -> {
                    if (customerId != order.getCustomer().getId()) {
                        throw new CustomerNotFoundException("Id customer unsuitable by order: customerId=" + customerId);
                    }
                    return order;
                })
                .findFirst()
                .orElseThrow();
    }

    /**
     * Метод создает и сохраняет цену и количество товара.
     *
     * @param product   - продукт
     * @param saveOrder - заказ
     * @param amount    - количество товара
     */
    private void addOrderItem(Product product, Order saveOrder, Integer amount) {
        orderItemRepository.saveAndFlush(new OrderItem
                (new OrderItemPk(saveOrder, product),
                        amount,
                        product.getPrice()));
    }
}
