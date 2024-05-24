package team.mediasoft.wareshop.entity.dto.order;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderDtoIfo(UUID orderId,
                          List<OrderProductDtoInfo> products,
                          BigDecimal totalPrice) {
}
