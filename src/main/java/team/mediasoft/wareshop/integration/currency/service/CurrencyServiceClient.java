package team.mediasoft.wareshop.integration.currency.service;

import team.mediasoft.wareshop.integration.currency.model.ExchangeRate;

public interface CurrencyServiceClient {
    ExchangeRate getExchangeRate();
}
