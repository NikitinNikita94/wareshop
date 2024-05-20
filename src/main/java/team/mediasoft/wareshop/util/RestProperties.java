package team.mediasoft.wareshop.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.rest.currency-service")
public record RestProperties(String host, List<String> methods, String file) {
}
