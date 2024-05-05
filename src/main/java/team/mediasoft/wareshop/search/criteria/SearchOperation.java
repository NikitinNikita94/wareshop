package team.mediasoft.wareshop.search.criteria;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SearchOperation {
    EQUAL,
    GRATER_THAN_OR_EQ,
    LESS_THAN_OR_EQ,
    LIKE;

    @JsonCreator
    public static SearchOperation getSimpleOperation(final String value) {
        return switch (value) {
            case "=", "EQUAL" -> EQUAL;
            case ">=", "GRATER_THAN_OR_EQ" -> GRATER_THAN_OR_EQ;
            case "<=", "LESS_THAN_OR_EQ" -> LESS_THAN_OR_EQ;
            case "~", "LIKE" -> LIKE;
            default -> null;
        };
    }
}
