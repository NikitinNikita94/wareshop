package team.mediasoft.wareshop.entity.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateOrderDto {
    @NotBlank
    @Size(min = 3, max = 128)
    private String deliveryAddress;

    @NotNull
    @Valid
    private List<CreateOrderProductDtoInfo> products;
}
