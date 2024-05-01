package team.mediasoft.wareshop.exchanger;

import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.entity.dto.ProductDtoInfo;
import team.mediasoft.wareshop.exchanger.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static team.mediasoft.wareshop.exchanger.filter.OncePerRequestFilter.RUB_CURRENCY;

@Component
public class ExchangeConvert {
    public static ProductDtoInfo convertExchangeRate(ProductDtoInfo dto, String currencyRate, Currency currency) {
        BigDecimal rate = switch (currencyRate) {
            case "CNY" -> Objects.requireNonNull(currency).getCny();
            case "USD" -> Objects.requireNonNull(currency).getUsd();
            case "EUR" -> Objects.requireNonNull(currency).getEur();
            default -> null;
        };

        if (!currencyRate.equals(RUB_CURRENCY)) {
            dto.setPrice(dto.getPrice().divide(rate, RoundingMode.HALF_EVEN));
        }
        dto.setCurrency(currencyRate);
        return dto;
    }
}
