package team.mediasoft.wareshop.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderDto;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderProductDtoInfo;
import team.mediasoft.wareshop.entity.dto.order.OrderDtoIfo;
import team.mediasoft.wareshop.entity.dto.order.OrderInfo;
import team.mediasoft.wareshop.entity.dto.order.OrderStatusDto;
import team.mediasoft.wareshop.entity.dto.order.ReadOrderDto;
import team.mediasoft.wareshop.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Эндпоинты для работы с заказами")
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать новый заказ",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            }
    )
    public void createOrder(@RequestHeader(value = "customerId") long customerId,
                            @RequestBody @Validated CreateOrderDto createOrderDto) {
        orderService.createOrder(customerId, createOrderDto);
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновить заказ по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Запрос выполнен"
                    )
            }
    )
    public void updateOrder(@RequestHeader(value = "customerId") long customerId,
                            @PathVariable @Parameter(description = "Идентификатор заказа") UUID orderId,
                            @RequestBody List<CreateOrderProductDtoInfo> createOrderProductDtoInfo) {
        orderService.updateOrder(customerId, orderId, createOrderProductDtoInfo);
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Найти заказ по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDtoIfo.class)
                            )
                    )
            }
    )
    public OrderDtoIfo getOrderById(@RequestHeader(value = "customerId") long customerId,
                                    @PathVariable @Parameter(description = "Идентификатор заказа") UUID orderId) {
        return orderService.findOrderById(customerId, orderId);
    }

    @DeleteMapping("/{orderId}")
    @Operation(
            summary = "Удалить заказ",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Заказ успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Заказ не найден по идентификатору"
                    )
            }
    )
    public void delete(@RequestHeader(value = "customerId") long customerId,
                       @PathVariable("orderId") @Parameter(description = "Идентификатор заказа") UUID orderId) {
        orderService.delete(customerId, orderId);
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDtoIfo> createOrderConfirm(@RequestHeader(value = "customerId") long customerId) {
        //TODO: - the service method does nothing
        return null;
    }

    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновить статус заказа по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Запрос выполнен"
                    )
            }
    )
    public ReadOrderDto updateOrderStatus(@RequestBody OrderStatusDto status,
                                          @PathVariable @Parameter(description = "Идентификатор заказа") UUID orderId) {
        return orderService.updateStatus(status, orderId);
    }

    @GetMapping(value = "/info/product-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<UUID, List<OrderInfo>> getOrderInformation() {
        return orderService.getOrderInfo();
    }
}
