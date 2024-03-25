package team.mediasoft.wareshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mediasoft.wareshop.data.repository.ProductRepository;
import team.mediasoft.wareshop.entity.dto.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.ProductReadDto;
import team.mediasoft.wareshop.mapper.ProductCreateEditMapper;
import team.mediasoft.wareshop.mapper.ProductReadMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCreateEditMapper createEditMapper;
    private final ProductReadMapper readMapper;

    /**
     * Метод возвращает коллекцию продуктов(товаров)
     *
     * @return List<ProductReadDto> - коллекция продуктов(товаров)
     */
    public List<ProductReadDto> findAll() {
        return productRepository.findAll().stream()
                .map(readMapper::map)
                .toList();
    }

    /**
     * Метод возвращает продукт(товар) по идентификатору
     *
     * @param id - идентификатор товара
     * @return Optional<ProductReadDto>
     */
    public Optional<ProductReadDto> findById(UUID id) {
        return productRepository.findById(id)
                .map(readMapper::map);
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
                .map(createEditMapper::map)
                .map(productRepository::save)
                .map(readMapper::map)
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
    public Optional<ProductReadDto> update(UUID id, ProductCreateEditDto productDto) {
        return productRepository.findById(id)
                .map(entity -> createEditMapper.mapUpdate(productDto, entity))
                .map(productRepository::saveAndFlush)
                .map(readMapper::map);
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

}
