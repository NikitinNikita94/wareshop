package team.mediasoft.wareshop.exchanger;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import team.mediasoft.wareshop.entity.dto.ProductDtoInfo;
import team.mediasoft.wareshop.exchanger.model.Currency;
import team.mediasoft.wareshop.exchanger.model.CurrencyProvider;

import java.math.BigDecimal;

import static team.mediasoft.wareshop.exchanger.ExchangeConvert.convertExchangeRate;

@Service
@ConditionalOnExpression("${app.currency-service.stub-enabled} == true")
@AllArgsConstructor
public class CurrencyServiceClientMock implements CurrencyServiceClient {
    private CurrencyProvider currencyProvider;

    @Override
    public ProductDtoInfo setExchangeRate(ProductDtoInfo dto) {
        Currency currency = getCurrency();
        String currencyRate = currencyProvider.getRate();
        return convertExchangeRate(dto, currencyRate, currency);
    }

    public Currency getCurrency() {
        return Currency.builder()
                .cny(BigDecimal.valueOf(8.10))
                .usd(BigDecimal.valueOf(89.0))
                .eur(BigDecimal.valueOf(100.0))
                .build();
    }
}
