package team.mediasoft.wareshop.exchanger;

import team.mediasoft.wareshop.entity.dto.ProductDtoInfo;

public interface CurrencyServiceClient {
    ProductDtoInfo setExchangeRate(ProductDtoInfo dto);
}
