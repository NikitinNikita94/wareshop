package team.mediasoft.wareshop.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderProductDtoInfo;
import team.mediasoft.wareshop.kafka.KafkaEvent;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderEventData implements KafkaEvent {

    private Event event;
    private Long customerId;
    private String deliveryAddress;
    private List<CreateOrderProductDtoInfo> products;
}
