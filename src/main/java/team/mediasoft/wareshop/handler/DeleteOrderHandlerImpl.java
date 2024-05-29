package team.mediasoft.wareshop.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import team.mediasoft.wareshop.service.OrderService;
import team.mediasoft.wareshop.web.request.DeleteOrderEventData;
import team.mediasoft.wareshop.web.request.Event;
import team.mediasoft.wareshop.web.request.EventSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteOrderHandlerImpl<T extends EventSource> implements EventHandler<DeleteOrderEventData> {

    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.DELETE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(DeleteOrderEventData eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");

        orderService.delete(eventSource.getCustomerId(), eventSource.getOrderId());

        log.info("Event handled: {}", eventSource);
        return eventSource.getEvent().name();
    }
}
