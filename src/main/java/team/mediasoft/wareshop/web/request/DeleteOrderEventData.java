package team.mediasoft.wareshop.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import team.mediasoft.wareshop.kafka.KafkaEvent;

import java.util.UUID;

@Data
@NoArgsConstructor
public class DeleteOrderEventData implements KafkaEvent {

    private Event event;
    private Long customerId;
    private UUID orderId;
}
