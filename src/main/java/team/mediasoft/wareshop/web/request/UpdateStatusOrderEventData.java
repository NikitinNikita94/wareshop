package team.mediasoft.wareshop.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import team.mediasoft.wareshop.entity.enumeration.OrderStatus;
import team.mediasoft.wareshop.kafka.KafkaEvent;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UpdateStatusOrderEventData implements KafkaEvent {

    private Event event;
    private UUID orderId;
    private OrderStatus status;
}
