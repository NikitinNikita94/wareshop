package team.mediasoft.wareshop.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import team.mediasoft.wareshop.entity.dto.ProductCreateEditDto;
import team.mediasoft.wareshop.entity.dto.ProductReadDto;
import team.mediasoft.wareshop.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Продукты", description = "Эндпоинты для работы с продуктами")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Получить список всех продуктов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductReadDto.class))
                            )
                    )
            }
    )
    public List<ProductReadDto> findAll() {
        return productService.findAll();
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
                                    schema = @Schema(implementation = ProductReadDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductReadDto> findById(@PathVariable("id") @Parameter(description = "Идентификатор продукта") UUID id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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
                                    schema = @Schema(implementation = ProductReadDto.class)
                            )
                    )
            }
    )
    public ProductReadDto create(@RequestBody @Validated ProductCreateEditDto user) {
        return productService.create(user);
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
                                    schema = @Schema(implementation = ProductReadDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductReadDto> update(@PathVariable("id") @Parameter(description = "Идентификатор продукта") UUID id,
                                                 @RequestBody @Validated ProductCreateEditDto product) {
        return productService.update(id, product)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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

}
