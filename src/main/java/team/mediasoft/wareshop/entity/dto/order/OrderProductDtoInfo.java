package team.mediasoft.wareshop.entity.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderProductDtoInfo(UUID productId, String name, Integer quantity, BigDecimal price) {
}
