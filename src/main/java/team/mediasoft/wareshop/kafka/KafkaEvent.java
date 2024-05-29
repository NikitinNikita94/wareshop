package team.mediasoft.wareshop.kafka;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import team.mediasoft.wareshop.web.request.CreateOrderEventData;
import team.mediasoft.wareshop.web.request.DeleteOrderEventData;
import team.mediasoft.wareshop.web.request.EventSource;
import team.mediasoft.wareshop.web.request.UpdateOrderEventData;
import team.mediasoft.wareshop.web.request.UpdateStatusOrderEventData;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        visible = true,
        include = JsonTypeInfo.As.PROPERTY,
        property = "event"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateOrderEventData.class, name = "CREATE_ORDER"),
        @JsonSubTypes.Type(value = UpdateOrderEventData.class, name = "UPDATE_ORDER"),
        @JsonSubTypes.Type(value = DeleteOrderEventData.class, name = "DELETE_ORDER"),
        @JsonSubTypes.Type(value = UpdateStatusOrderEventData.class, name = "UPDATE_ORDER_STATUS")
})
public interface KafkaEvent extends EventSource {
}
