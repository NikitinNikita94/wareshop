package team.mediasoft.wareshop.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import team.mediasoft.wareshop.service.OrderService;
import team.mediasoft.wareshop.web.request.Event;
import team.mediasoft.wareshop.web.request.EventSource;
import team.mediasoft.wareshop.web.request.UpdateOrderEventData;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateOrderHandlerImpl<T extends EventSource> implements EventHandler<UpdateOrderEventData> {

    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.UPDATE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(UpdateOrderEventData eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");

        orderService.updateOrder(eventSource.getCustomerId(),
                eventSource.getOrderId(),
                eventSource.getProducts());

        log.info("Event handled: {}", eventSource);
        return eventSource.getEvent().name();
    }
}
