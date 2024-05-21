package team.mediasoft.wareshop.businesslogic.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import team.mediasoft.wareshop.util.RestProperties;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(name = "app.rest.currency-service.mock.enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class CustomerAccountServiceImpl implements CustomerAccountService {

    private final RestProperties properties;
    private final WebClient webClient;

    @Override
    public Map<String, String> getBlockAccountCustomersByLogins(List<String> logins) {
        return webClient
                .post()
                .uri(properties.methods().get(1))
                .bodyValue(logins)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .onErrorResume(err -> Flux.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                .blockFirst();
    }

    @Override
    public CompletableFuture<Map<String, String>> getNonBlockAccountCustomersByLogins(List<String> logins) {
        return webClient
                .post()
                .uri(properties.methods().get(1))
                .bodyValue(logins)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .onErrorResume(err -> Flux.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                .singleOrEmpty().toFuture();
    }
}