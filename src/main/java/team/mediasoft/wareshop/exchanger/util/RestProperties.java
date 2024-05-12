package team.mediasoft.wareshop.exchanger.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rest.currency-service")
public record RestProperties(String host, String method, String file) {
}
