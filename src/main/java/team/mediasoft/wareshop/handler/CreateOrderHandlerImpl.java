package team.mediasoft.wareshop.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import team.mediasoft.wareshop.entity.dto.order.CreateOrderDto;
import team.mediasoft.wareshop.service.OrderService;
import team.mediasoft.wareshop.web.request.CreateOrderEventData;
import team.mediasoft.wareshop.web.request.Event;
import team.mediasoft.wareshop.web.request.EventSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderHandlerImpl<T extends EventSource> implements EventHandler<CreateOrderEventData> {

    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.CREATE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(CreateOrderEventData eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");

        orderService.createOrder(eventSource.getCustomerId(),
                CreateOrderDto.builder()
                        .deliveryAddress(eventSource.getDeliveryAddress())
                        .products(eventSource.getProducts())
                        .build());

        log.info("Event handled: {}", eventSource);

        return eventSource.getEvent().name();
    }
}
