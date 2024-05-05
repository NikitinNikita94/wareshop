package team.mediasoft.wareshop.entity.dto.product;

import lombok.Getter;
import lombok.Setter;
import team.mediasoft.wareshop.entity.enumeration.ProductCategory;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDtoInfo {
    private String name;
    private String description;
    private ProductCategory category;
    private BigDecimal price;
    private Integer amount;
}
