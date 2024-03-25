package team.mediasoft.wareshop.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Value;
import team.mediasoft.wareshop.entity.ProductCategory;

import java.math.BigDecimal;

@Value
public class ProductCreateEditDto {

    @NotBlank
    @Size(min = 3, max = 64)
    String name;

    @PositiveOrZero
    @Max(value = 50_000)
    @Min(value = 1)
    Integer vendorCode;

    @NotBlank
    @Size(min = 3, max = 256)
    String description;

    @NotNull
    ProductCategory category;

    @PositiveOrZero
    @Max(value = 20_000_000)
    @Min(value = 10_000)
    BigDecimal price;

    @PositiveOrZero
    @Max(value = 10_000)
    Integer amount;
}
