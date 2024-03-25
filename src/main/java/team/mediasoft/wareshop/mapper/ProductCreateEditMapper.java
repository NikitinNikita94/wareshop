package team.mediasoft.wareshop.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.entity.dto.ProductCreateEditDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductCreateEditMapper implements Mapper<ProductCreateEditDto, Product> {
    @Override
    public Product map(ProductCreateEditDto object) {
        Product product = new Product();
        copy(object, product);
        return product;
    }

    @Override
    public Product map(ProductCreateEditDto fromObject, Product toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    public Product mapUpdate(ProductCreateEditDto object, Product toUpdateProduct) {
        copyUpdate(object, toUpdateProduct);
        return toUpdateProduct;
    }

    private static void copy(ProductCreateEditDto object, Product product) {
        copyUpdate(object, product);
        product.setCreateAt(LocalDateTime.now());
    }

    private static void copyUpdate(ProductCreateEditDto object, Product product) {
        product.setName(object.getName());
        product.setVendorCode(object.getVendorCode());
        product.setDescription(object.getDescription());
        product.setCategory(object.getCategory());
        product.setPrice(object.getPrice());
        product.setAmount(object.getAmount());
        product.setLastUpdateAmount(LocalDateTime.now());
    }

}
