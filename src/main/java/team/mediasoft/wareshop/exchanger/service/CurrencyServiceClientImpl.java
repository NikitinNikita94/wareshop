package team.mediasoft.wareshop.exchanger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import team.mediasoft.wareshop.exchanger.model.ExchangeRate;
import team.mediasoft.wareshop.util.RestProperties;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceClientImpl implements CurrencyServiceClient {

    private final RestProperties properties;
    private final WebClient webClient;

    @Override
    @Cacheable("exchangeRate")
    public ExchangeRate getExchangeRate() {
        return webClient
                .get()
                .uri(properties.methods().get(0))
                .retrieve()
                .bodyToFlux(ExchangeRate.class)
                .onErrorResume(err -> Flux.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
                .blockFirst();
    }
}
