package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import team.mediasoft.wareshop.entity.dto.product.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.product.ProductReadDto;
import team.mediasoft.wareshop.entity.dto.product.ProductUpdateDto;
import team.mediasoft.wareshop.entity.enumeration.ProductCategory;
import team.mediasoft.wareshop.integration.IntegrationTestBase;
import team.mediasoft.wareshop.search.criteria.BigDecimalSearchCriteria;
import team.mediasoft.wareshop.search.criteria.LocalDateSearchCriteria;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;
import team.mediasoft.wareshop.search.criteria.SearchOperation;
import team.mediasoft.wareshop.search.criteria.StringSearchCriteria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
class ProductServiceTest extends IntegrationTestBase {

    private static final UUID PRODUCT_UUID_1 = fromString("54a36c6d-eeea-4249-891f-70001ac6ad1e");
    public static final String PRODUCT_NAME_1 = "Iphone";
    private final ProductService productService;

    @Test
    void findAllTest() {
        List<ProductReadDto> result = productService.findAll(Pageable.ofSize(10));
        Optional<ProductReadDto> actual = Optional.of(productService.findById(PRODUCT_UUID_1));

        assertThat(result).hasSize(3);
        actual.ifPresent(product -> assertEquals(result.get(0).getName(), product.getName()));
        actual.ifPresent(product -> assertEquals(result.get(0).getDescription(), product.getDescription()));
        actual.ifPresent(product -> assertEquals(result.get(0).getVendorCode(), product.getVendorCode()));
        actual.ifPresent(product -> assertEquals(result.get(0).getPrice(), product.getPrice()));
        actual.ifPresent(product -> assertEquals(result.get(0).getCategory(), product.getCategory()));
        actual.ifPresent(product -> assertEquals(result.get(0).getLastUpdateAmount(), product.getLastUpdateAmount()));
        actual.ifPresent(product -> assertEquals(result.get(0).getCreateAt(), product.getCreateAt()));
        actual.ifPresent(product -> assertEquals(result.get(0).getCategory(), product.getCategory()));
    }

    @Test
    void findByIdTest() {
        Optional<ProductReadDto> maybeProduct = Optional.of(productService.findById(PRODUCT_UUID_1));
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
        ProductUpdateDto productUpdateDto = new ProductUpdateDto(
                "Телефон LG",
                252,
                "Мобильный телефон",
                ProductCategory.MOBILE,
                BigDecimal.valueOf(44_098.90),
                35
        );
        Optional<ProductReadDto> actualResult = productService.update(PRODUCT_UUID_1, productUpdateDto);
        assertTrue(actualResult.isPresent());

        actualResult.ifPresent(product -> {
            assertEquals(productUpdateDto.getName(), product.getName());
            assertEquals(productUpdateDto.getVendorCode(), product.getVendorCode());
            assertEquals(productUpdateDto.getDescription(), product.getDescription());
            assertEquals(productUpdateDto.getCategory(), product.getCategory());
            assertEquals(productUpdateDto.getPrice(), product.getPrice());
            assertEquals(productUpdateDto.getAmount(), product.getAmount());
        });
    }

    @Test
    void deleteTest() {
        assertFalse(productService.delete(fromString("64a36c6d-eeea-4249-891f-70071ac7ad1e")));
        assertTrue(productService.delete(PRODUCT_UUID_1));
    }

    @Test
    @SneakyThrows
    void findBySearchCriteria() {
        List<SearchCriteria> searchCriteria = List.of(
                new BigDecimalSearchCriteria("price", SearchOperation.GRATER_THAN_OR_EQ, BigDecimal.valueOf(400)),
                new LocalDateSearchCriteria("createAt", SearchOperation.EQUAL, LocalDate.of(2015, 7, 30)),
                new BigDecimalSearchCriteria("price", SearchOperation.LESS_THAN_OR_EQ, BigDecimal.valueOf(70000)),
                new StringSearchCriteria("name", SearchOperation.LIKE, "Ip"));

        List<ProductReadDto> bySearchCriteriaDto = productService.findBySearchCriteria(searchCriteria, Pageable.ofSize(2));
        assertNotNull(bySearchCriteriaDto);
        assertEquals(1, bySearchCriteriaDto.size());
    }
}