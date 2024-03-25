package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import team.mediasoft.wareshop.entity.ProductCategory;
import team.mediasoft.wareshop.entity.dto.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.ProductReadDto;
import team.mediasoft.wareshop.integration.IntegrationTestBase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class ProductServiceTest extends IntegrationTestBase {

    private static final UUID PRODUCT_UUID_1 = fromString("54a36c6d-eeea-4249-891f-70001ac6ad1e");
    public static final String PRODUCT_NAME_1 = "Iphone";
    private final ProductService productService;

    @Test
    void findAllTest() {
        List<ProductReadDto> result = productService.findAll();
        Optional<ProductReadDto> actual = productService.findById(PRODUCT_UUID_1);

        assertThat(result).hasSize(3);
        actual.ifPresent(product -> assertEquals(result.get(0), product));
    }


    @Test
    void findByIdTest() {
        Optional<ProductReadDto> maybeProduct = productService.findById(PRODUCT_UUID_1);
        assertTrue(maybeProduct.isPresent());
        maybeProduct.ifPresent(product -> assertEquals(PRODUCT_NAME_1, product.getName()));
    }

    @Test
    void createTest() {
        ProductCreateEditDto productDto = new ProductCreateEditDto(
                "Телефон",
                25,
                "Мобильный телефон",
                ProductCategory.MOBILE,
                BigDecimal.valueOf(34_098.90),
                3
        );
        ProductReadDto actualResult = productService.create(productDto);
        assertNotNull(actualResult);
        assertEquals(productDto.getName(), actualResult.getName());
        assertEquals(productDto.getVendorCode(), actualResult.getVendorCode());
        assertEquals(productDto.getDescription(), actualResult.getDescription());
        assertEquals(productDto.getCategory(), actualResult.getCategory());
        assertEquals(productDto.getPrice(), actualResult.getPrice());
        assertEquals(productDto.getAmount(), actualResult.getAmount());
    }

    @Test
    void updateTest() {
        ProductCreateEditDto productDto = new ProductCreateEditDto(
                "Телефон LG",
                252,
                "Мобильный телефон",
                ProductCategory.MOBILE,
                BigDecimal.valueOf(44_098.90),
                35
        );
        Optional<ProductReadDto> actualResult = productService.update(PRODUCT_UUID_1, productDto);
        assertTrue(actualResult.isPresent());

        actualResult.ifPresent(product -> {
            assertEquals(productDto.getName(), product.getName());
            assertEquals(productDto.getVendorCode(), product.getVendorCode());
            assertEquals(productDto.getDescription(), product.getDescription());
            assertEquals(productDto.getCategory(), product.getCategory());
            assertEquals(productDto.getPrice(), product.getPrice());
            assertEquals(productDto.getAmount(), product.getAmount());
        });
    }

    @Test
    void deleteTest() {
        assertFalse(productService.delete(fromString("64a36c6d-eeea-4249-891f-70071ac7ad1e")));
        assertTrue(productService.delete(PRODUCT_UUID_1));
    }
}