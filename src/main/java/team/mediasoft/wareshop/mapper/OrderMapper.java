package team.mediasoft.wareshop.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.data.repository.CustomerRepository;
import team.mediasoft.wareshop.data.repository.OrderRepository;
import team.mediasoft.wareshop.entity.Order;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderDto;
import team.mediasoft.wareshop.entity.dto.order.OrderDtoIfo;
import team.mediasoft.wareshop.entity.dto.order.OrderProductDtoInfo;
import team.mediasoft.wareshop.entity.dto.order.ReadOrderDto;
import team.mediasoft.wareshop.entity.enumeration.OrderStatus;
import team.mediasoft.wareshop.exception.CustomerNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public Order toCreateOrder(CreateOrderDto createOrderDto, Long oldCustomer) {
        return Order.builder()
                .customer(customerRepository.findById(oldCustomer)
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found")))
                .status(OrderStatus.CREATED)
                .deliveryAddress(createOrderDto.getDeliveryAddress())
                .build();
    }

    public ReadOrderDto toReadOrderDto(Order order) {
        return ReadOrderDto.builder()
                .customerId(order.getCustomer().getId())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .build();
    }

    public OrderDtoIfo toOrderDtoIfo(Order order) {
        List<OrderProductDtoInfo> productsByOrderId = orderRepository.getAllProductsByOrderId(order.getId());

        BigDecimal totalPrice = productsByOrderId.stream()
                .map(OrderProductDtoInfo::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderDtoIfo.builder()
                .orderId(order.getId())
                .products(productsByOrderId)
                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
