package team.mediasoft.wareshop.entity.dto.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDtoIfo(UUID orderId,
                          List<OrderProductDtoInfo> products,
                          BigDecimal totalPrice) {
}
