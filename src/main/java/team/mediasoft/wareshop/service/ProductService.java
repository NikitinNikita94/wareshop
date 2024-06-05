package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.dto.product.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.product.ProductReadDto;
import team.mediasoft.wareshop.entity.dto.product.ProductUpdateDto;
import team.mediasoft.wareshop.exception.ProductNotFoundException;
import team.mediasoft.wareshop.integration.currency.model.Currency;
import team.mediasoft.wareshop.integration.currency.model.CurrencyProvider;
import team.mediasoft.wareshop.integration.currency.service.ExchangeRateProvider;
import team.mediasoft.wareshop.mapper.ProductMapper;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;
import team.mediasoft.wareshop.search.specification.ProductSpecification;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ExchangeRateProvider rateProvider;
    private final CurrencyProvider currencyProvider;

    /**
     * Метод возвращает коллекцию продуктов(товаров)
     *
     * @return List<ProductReadDto> - коллекция продуктов(товаров)
     */
    public List<ProductReadDto> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).stream()
                .map(ProductMapper.INSTANCE::productToProductReadDto)
                .map(this::setPriceAndCurrencyToDto)
                .toList();
    }

    /**
     * Метод возвращает продукт(товар) по идентификатору
     *
     * @param id - идентификатор товара
     * @return Optional<ProductReadDto>
     */
    public ProductReadDto findById(UUID id) {
        return productRepository.findById(id)
                .map(ProductMapper.INSTANCE::productToProductReadDto)
                .map(this::setPriceAndCurrencyToDto)
                .orElseThrow(() -> new ProductNotFoundException("Can't find a product with this id = " + id));
    }

    /**
     * Метод по созданию продукта(товара)
     *
     * @param productDto - dto с информацией о продукте(товаре)
     * @return ProductReadDto - сохраненный продукт(товар)
     */
    @Transactional
    public ProductReadDto create(ProductCreateEditDto productDto) {
        return Optional.of(productDto)
                .map(ProductMapper.INSTANCE::productCreateEditDtoToProduct)
                .map(productRepository::save)
                .map(ProductMapper.INSTANCE::productToProductReadDto)
                .orElseThrow();
    }

    /**
     * Метод для обновления продукта(товара) по идентификатору
     *
     * @param id         - идентификатор продукта(товара)
     * @param productDto - dto с информацией для обновления
     * @return Optional<ProductReadDto> - обновленный продукт(товар)
     */
    @Transactional
    public Optional<ProductReadDto> update(UUID id, ProductUpdateDto productDto) {
        return Optional.ofNullable(productRepository.findById(id)
                .filter(entity -> Objects.equals(productDto.getVendorCode(), entity.getVendorCode()))
                .map(entity -> ProductMapper.INSTANCE.productUpdateDtoToUpdateProduct(entity, productDto))
                .map(productRepository::saveAndFlush)
                .map(ProductMapper.INSTANCE::productToProductReadDto)
                .orElseThrow(() -> new ProductNotFoundException("Can't find a product with this id = " + id)));
    }

    /**
     * Метод по удалению продукта(товара)
     *
     * @param id - идентификатор продукта(товара)
     * @return boolean - если обновлен true в противном случае false
     */
    @Transactional
    public boolean delete(UUID id) {
        return productRepository.findById(id)
                .map(entity -> {
                    productRepository.delete(entity);
                    productRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    /**
     * Находит товары по критериям поиска.
     *
     * @param searchCriteria спецификация критериев поиска
     * @param pageable       информация о страницах
     * @return список продуктов, которые соответствуют критериям поиска
     */
    public List<ProductReadDto> findBySearchCriteria(List<SearchCriteria> searchCriteria, Pageable pageable) {
        ProductSpecification specification = new ProductSpecification(searchCriteria);
        return productRepository.findAll(specification.buildSpecification(), pageable).stream()
                .map(ProductMapper.INSTANCE::productToProductReadDto)
                .toList();
    }

    private ProductReadDto setPriceAndCurrencyToDto(ProductReadDto dto) {
        if (currencyProvider.getCurrency() == null) {
            currencyProvider.setCurrency(Currency.RUB);
        }
        if (!currencyProvider.getCurrency().equals(Currency.RUB)) {
            dto.setPrice(
                    dto.getPrice().divide(
                            rateProvider.getExchangeRate(currencyProvider.getCurrency()),
                            RoundingMode.HALF_EVEN));
        }
        dto.setCurrency(currencyProvider.getCurrency());
        return dto;
    }
}
