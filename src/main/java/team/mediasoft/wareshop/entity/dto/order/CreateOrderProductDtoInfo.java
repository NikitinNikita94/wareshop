package team.mediasoft.wareshop.entity.dto.order;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

public record CreateOrderProductDtoInfo(@NotNull UUID productId, @Range(min = 1, max = 500) Integer quantity) {
}
