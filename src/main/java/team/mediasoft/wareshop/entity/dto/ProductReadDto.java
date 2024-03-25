package team.mediasoft.wareshop.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import team.mediasoft.wareshop.entity.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ProductReadDto {

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

    @DateTimeFormat(pattern = "YYYY-MM-DD HH:MM:SS")
    LocalDateTime lastUpdateAmount;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:MM:SS")
    LocalDateTime createAt;
}
