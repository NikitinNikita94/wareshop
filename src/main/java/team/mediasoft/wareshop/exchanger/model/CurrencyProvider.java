package team.mediasoft.wareshop.exchanger.model;

import lombok.Data;
import org.springframework.cache.annotation.CachePut;

@Data
public class CurrencyProvider {
    private String rate;

    @CachePut(cacheNames = "rate", key = "#currencyRate")
    public void updateCacheRate(String currencyRate) {
        rate = currencyRate;
    }
}
