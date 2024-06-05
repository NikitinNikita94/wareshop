package team.mediasoft.wareshop.integration.currency.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Data
@SessionScope
public class CurrencyProvider {
    private Currency currency;
}
