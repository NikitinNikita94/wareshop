package team.mediasoft.wareshop.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderProductDtoInfo;
import team.mediasoft.wareshop.kafka.KafkaEvent;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UpdateOrderEventData implements KafkaEvent {

    private Event event;
    private Long customerId;
    private UUID orderId;
    private List<CreateOrderProductDtoInfo> products;
}
