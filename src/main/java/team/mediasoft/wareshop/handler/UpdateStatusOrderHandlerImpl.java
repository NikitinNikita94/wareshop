package team.mediasoft.wareshop.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import team.mediasoft.wareshop.entity.dto.order.OrderStatusDto;
import team.mediasoft.wareshop.service.OrderService;
import team.mediasoft.wareshop.web.request.Event;
import team.mediasoft.wareshop.web.request.EventSource;
import team.mediasoft.wareshop.web.request.UpdateStatusOrderEventData;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateStatusOrderHandlerImpl<T extends EventSource> implements EventHandler<UpdateStatusOrderEventData> {

    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.UPDATE_ORDER_STATUS.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(UpdateStatusOrderEventData eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");

        orderService.updateStatus(new OrderStatusDto(eventSource.getStatus()),
                eventSource.getOrderId());

        log.info("Event handled: {}", eventSource);
        return eventSource.getEvent().name();
    }
}
