package team.mediasoft.wareshop.entity.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import team.mediasoft.wareshop.entity.ProductCategory;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDto {

    @NotBlank
    @Size(min = 3, max = 64)
    String name;

    @PositiveOrZero
    @Max(value = 50_000)
    Integer vendorCode;

    @NotBlank
    @Size(min = 3, max = 256)
    String description;

    @NotNull
    ProductCategory category;

    @PositiveOrZero
    @Max(value = 20_000_000)
    BigDecimal price;

    @PositiveOrZero
    @Max(value = 10_000)
    Integer amount;

}