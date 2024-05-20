package team.mediasoft.wareshop.exchanger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.exchanger.model.Currency;
import team.mediasoft.wareshop.exchanger.model.ExchangeRate;
import team.mediasoft.wareshop.util.RestProperties;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateProvider {

    private final CurrencyServiceClient currencyServiceClient;
    private final ObjectMapper mapper;
    private final RestProperties properties;

    public BigDecimal getExchangeRate(Currency currency) {
        return Optional.ofNullable(getExchangeRateFromService(currency))
                .orElseGet(() -> getExchangeRateFromFile(currency));
    }

    private @Nullable BigDecimal getExchangeRateFromService(Currency currency) {
        log.info("Получаем курс из второго сервиса или из кэша");
        return Optional.ofNullable(currencyServiceClient.getExchangeRate())
                .map(rate -> getExchangeRateByCurrency(rate, currency)).orElse(null);
    }

    @SneakyThrows
    private BigDecimal getExchangeRateFromFile(Currency currency) {
        log.info("Получаем курс из файла");
        ExchangeRate exchangeRate = mapper.readValue(Path.of(properties.file()).toFile(), ExchangeRate.class);
        return getExchangeRateByCurrency(exchangeRate, currency);
    }

    private BigDecimal getExchangeRateByCurrency(ExchangeRate exchangeRate, Currency currency) {
        return switch (currency) {
            case USD -> exchangeRate.getUsd();
            case CNY -> exchangeRate.getCny();
            case EUR -> exchangeRate.getEur();
            case RUB -> BigDecimal.ONE;
        };
    }
}
