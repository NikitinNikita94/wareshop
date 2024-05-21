package team.mediasoft.wareshop.entity.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import team.mediasoft.wareshop.entity.dto.customer.CustomerInfo;
import team.mediasoft.wareshop.entity.enumeration.OrderStatus;

import java.util.UUID;

public record OrderInfo(@JsonProperty("id") UUID id,
                        @JsonProperty("customer") CustomerInfo customer,
                        @JsonProperty("status") OrderStatus status,
                        @JsonProperty("deliveryAddress") String deliveryAddress,
                        @JsonProperty("quantity") Integer quantity) {
}
