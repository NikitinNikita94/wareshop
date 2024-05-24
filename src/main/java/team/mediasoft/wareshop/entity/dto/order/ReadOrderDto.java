package team.mediasoft.wareshop.entity.dto.order;

import lombok.Builder;
import team.mediasoft.wareshop.entity.enumeration.OrderStatus;

@Builder
public record ReadOrderDto(Long customerId, OrderStatus status, String deliveryAddress) {
}
