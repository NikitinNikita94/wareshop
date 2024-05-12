package team.mediasoft.wareshop.entity.dto;

import lombok.Getter;
import lombok.Setter;
import team.mediasoft.wareshop.entity.ProductCategory;
import team.mediasoft.wareshop.exchanger.model.Currency;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDtoInfo {
    private String name;
    private String description;
    private ProductCategory category;
    private BigDecimal price;
    private Integer amount;
    private Currency currency;
}
