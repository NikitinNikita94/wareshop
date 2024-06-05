package team.mediasoft.wareshop.integration.currency.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Currency {
    CNY,
    USD,
    EUR,
    RUB;

    @JsonCreator
    public static Currency getCurrency(final String value) {
        return switch (value) {
            case "CNY" -> CNY;
            case "USD" -> USD;
            case "EUR" -> EUR;
            case ""-> RUB;
            default -> null;
        };
    }
}
