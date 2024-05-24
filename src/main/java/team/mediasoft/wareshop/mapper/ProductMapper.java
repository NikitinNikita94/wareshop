package team.mediasoft.wareshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.entity.dto.product.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.product.ProductDtoInfo;
import team.mediasoft.wareshop.entity.dto.product.ProductReadDto;
import team.mediasoft.wareshop.entity.dto.product.ProductUpdateDto;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDtoInfo productReadDtoToProductDtoInfo(ProductReadDto productReadDto);

    @Mappings({
            @Mapping(target = "lastAmountUp", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "createAt", expression = "java(java.time.LocalDate.now())")
    })
    Product productCreateEditDtoToProduct(ProductCreateEditDto productCreateEditDto);

    Product productUpdateDtoToUpdateProduct(@MappingTarget Product product, ProductUpdateDto productUpdateDto);

    ProductReadDto productToProductReadDto(Product product);
}
