package team.mediasoft.wareshop.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import team.mediasoft.wareshop.handler.EventHandler;
import team.mediasoft.wareshop.web.request.EventSource;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "kafka.enabled", matchIfMissing = false)
public class Consumer {

    private final Set<EventHandler<EventSource>> eventHandlers;

    @SneakyThrows
    @KafkaListener(topics = "test_topic", groupId = "group1", containerFactory = "kafkaListenerContainerFactoryByte")
    public void listenGroupTopic2(byte[] message) throws JsonProcessingException {
        log.info("Receive message: {}", message);

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final KafkaEvent eventSource = objectMapper.readValue(message, KafkaEvent.class);
            log.info("EventSource: {}", eventSource);

            eventHandlers.stream()
                    .filter(eventSourceEventHandler -> eventSourceEventHandler.canHandle(eventSource))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Handler for eventsource not found"))
                    .handleEvent(eventSource);

        } catch (JsonProcessingException e) {
            log.error("Couldn't parse message: {}; exception: ", message, e);
        }
    }
}
