package team.mediasoft.wareshop.entity.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

public record CreateOrderProductDtoInfo(@NotNull @JsonProperty("id") UUID productId,
                                        @Range(min = 1, max = 500) @JsonProperty("quantity") Integer quantity) {
}
