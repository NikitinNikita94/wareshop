package team.mediasoft.wareshop.entity.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import team.mediasoft.wareshop.entity.enumeration.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReadDto {

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

    @DateTimeFormat(pattern = "YYYY-MM-DD HH:MM:SS")
    LocalDateTime lastUpdateAmount;
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    LocalDate createAt;

}
