package team.mediasoft.wareshop.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaProperties(String bootstrapAddress, String groupId) {
}
