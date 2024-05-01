package team.mediasoft.wareshop.exchanger;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import team.mediasoft.wareshop.entity.dto.ProductDtoInfo;
import team.mediasoft.wareshop.exchanger.model.Currency;
import team.mediasoft.wareshop.exchanger.model.CurrencyProvider;

import java.io.File;
import java.time.Duration;

import static team.mediasoft.wareshop.exchanger.ExchangeConvert.convertExchangeRate;

@Service
@Slf4j
@ConditionalOnExpression("${app.currency-service.stub-enabled} == false")
@RequiredArgsConstructor
public class CurrencyServiceClientImpl implements CurrencyServiceClient {

    @Value("${app.currency-service.methods.get-currency}")
    private String uri;
    @Value("${app.currency-service.exchangeJson}")
    private File currencyWithFile;
    private final ObjectMapper mapper;
    private final WebClient webClient;
    @Autowired
    private CurrencyProvider currencyProvider;

    @Override
    public ProductDtoInfo setExchangeRate(ProductDtoInfo dto) {
        Currency currency = null;
        if (getCurrency().blockFirst() == null) {
            currency = currencyFromJson();
        } else {
            log.info("Reading data from another service");
            currencyProvider.updateCacheRate(currencyProvider.getRate());
            currency = getCurrency().blockFirst();
        }
        String currencyProviderRate = currencyProvider.getRate();
        return convertExchangeRate(dto, currencyProviderRate, currency);
    }

    public Flux<Currency> getCurrency() {
        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Currency.class)
                .onErrorResume(err -> Mono.empty())
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)));
    }

    @SneakyThrows
    private Currency currencyFromJson() {
        log.info("Inside a method that reads from a file");
        return mapper.readValue(currencyWithFile, Currency.class);
    }
}
