package team.mediasoft.wareshop.exchanger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    @JsonProperty("CNY")
    private BigDecimal cny;
    @JsonProperty("USD")
    private BigDecimal usd;
    @JsonProperty("EUR")
    private BigDecimal eur;
}
