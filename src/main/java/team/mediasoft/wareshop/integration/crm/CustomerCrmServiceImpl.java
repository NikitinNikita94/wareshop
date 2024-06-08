package team.mediasoft.wareshop.integration.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import team.mediasoft.wareshop.util.RestProperties;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(name = "app.rest.currency-service.mock.enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class CustomerCrmServiceImpl implements CustomerCrmService {

    private final RestProperties properties;
    private final WebClient webClient;

    @Override
    public Map<String, String> getBlockCrmCustomersByLogins(List<String> logins) {
        return webClient
                .post()
                .uri(properties.methods().get(2))
                .bodyValue(logins)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .onErrorResume(err -> Mono.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                .block();
    }

    @Override
    public CompletableFuture<Map<String, String>> getNonBlockCrmCustomersByLogins(List<String> logins) {
        return webClient
                .post()
                .uri(properties.methods().get(2))
                .bodyValue(logins)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .onErrorResume(err -> Mono.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                .toFuture();
    }
}
