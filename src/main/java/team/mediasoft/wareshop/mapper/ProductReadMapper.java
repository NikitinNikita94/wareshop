package team.mediasoft.wareshop.mapper;

import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.entity.dto.ProductReadDto;

@Component
public class ProductReadMapper implements Mapper<Product, ProductReadDto> {
    @Override
    public ProductReadDto map(Product object) {
        return new ProductReadDto(
                object.getName(),
                object.getVendorCode(),
                object.getDescription(),
                object.getCategory(),
                object.getPrice(),
                object.getAmount(),
                object.getLastUpdateAmount(),
                object.getCreateAt()
        );
    }

}
