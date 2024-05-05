package team.mediasoft.wareshop.entity.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderProductDtoInfo {
    private UUID productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}
