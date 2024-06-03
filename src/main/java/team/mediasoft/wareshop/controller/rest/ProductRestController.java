package team.mediasoft.wareshop.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import team.mediasoft.wareshop.entity.dto.product.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.product.ProductDtoInfo;
import team.mediasoft.wareshop.entity.dto.product.ProductUpdateDto;
import team.mediasoft.wareshop.mapper.ProductMapper;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;
import team.mediasoft.wareshop.service.ProductMetaDataService;
import team.mediasoft.wareshop.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Продукты", description = "Эндпоинты для работы с продуктами")
public class ProductRestController {

    private final ProductService productService;
    private final ProductMetaDataService productMetaDataService;

    @GetMapping
    @Operation(
            summary = "Получить список всех продуктов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDtoInfo.class))
                            )
                    )
            }
    )
    public List<ProductDtoInfo> findAll(@PageableDefault(size = 40, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.findAll(pageable).stream()
                .map(ProductMapper.INSTANCE::productReadDtoToProductDtoInfo)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Найти продукт по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDtoInfo.class)
                            )
                    )
            }
    )
    public ProductDtoInfo findById(@PathVariable("id") @Parameter(description = "Идентификатор продукта") UUID id) {
        return ProductMapper.INSTANCE.productReadDtoToProductDtoInfo(productService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать новый продукт",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDtoInfo.class)
                            )
                    )
            }
    )
    public ProductDtoInfo create(@RequestBody @Validated ProductCreateEditDto user) {
        return ProductMapper.INSTANCE.productReadDtoToProductDtoInfo(productService.create(user));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить данные продукта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Продукт успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductDtoInfo.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductDtoInfo> update(@PathVariable("id") @Parameter(description = "Идентификатор продукта") UUID id,
                                                 @RequestBody @Validated ProductUpdateDto productUpdateDto) {
        return productService.update(id, productUpdateDto)
                .map(ProductMapper.INSTANCE::productReadDtoToProductDtoInfo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить продукт",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Продукт успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Продукт не найден по идентификатору"
                    )
            }
    )
    public ResponseEntity<?> delete(@PathVariable("id") @Parameter(description = "Идентификатор продукта") UUID id) {
        return productService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    @Operation(
            summary = "Поиск продуктов про критериям",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDtoInfo.class))
                            )
                    )
            }
    )
    public ResponseEntity<List<ProductDtoInfo>> searchProduct(@PageableDefault(size = 40, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                              @RequestBody List<SearchCriteria> searchCriteria) {
        List<ProductDtoInfo> productDtoInfos = productService.findBySearchCriteria(searchCriteria, pageable).stream()
                .map(ProductMapper.INSTANCE::productReadDtoToProductDtoInfo)
                .toList();

        return ResponseEntity.ok().body(productDtoInfos);
    }

    @PostMapping(path = "/files/upload")
    @Operation(
            summary = "Сохранить файлы по идентификатору продукта",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                            )
                    )
            }
    )
    public void uploadFile(@RequestParam("productId") UUID productId,
                           @RequestParam("file") MultipartFile... file) {
        productMetaDataService.uploadFile(productId, file);
    }

    @GetMapping(path = "/files/{productId}/download", produces = "application/zip")
    @Operation(
            summary = "Скачать файлы по идентификатору продукта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                            )
                    )
            }
    )
    public void downloadFile(@PathVariable("productId") UUID productId,
                             HttpServletResponse response) {
        productMetaDataService.downloadFiles(productId, response);
    }
}
