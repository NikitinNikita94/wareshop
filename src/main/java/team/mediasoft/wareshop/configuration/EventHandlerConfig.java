package team.mediasoft.wareshop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.mediasoft.wareshop.handler.EventHandler;
import team.mediasoft.wareshop.web.request.EventSource;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class EventHandlerConfig {

    @Bean
    <T extends EventSource> Set<EventHandler<T>> eventHandlers(Set<EventHandler<T>> eventHandlers) {
        return new HashSet<>(eventHandlers);
    }
}
