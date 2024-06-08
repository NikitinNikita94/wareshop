package team.mediasoft.wareshop.integration.currency.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import team.mediasoft.wareshop.integration.currency.model.ExchangeRate;

import java.math.BigDecimal;

@Service
@Primary
@ConditionalOnProperty(name = "${rest.currency-service.mock.enabled:false}")
public class CurrencyServiceClientMock implements CurrencyServiceClient {

    @Override
    public ExchangeRate getExchangeRate() {
        return ExchangeRate.builder()
                .cny(BigDecimal.valueOf(8.10))
                .usd(BigDecimal.valueOf(89.0))
                .eur(BigDecimal.valueOf(100.0))
                .build();
    }
}
