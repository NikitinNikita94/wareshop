package team.mediasoft.wareshop.search.common;

public enum SearchOperation {
    EQUAL("="),
    GRATER_THAN_OR_EQ(">="),
    LESS_THAN_OR_EQ("<="),
    LIKE("~");

    private final String value;

    SearchOperation(String value) {
        this.value = value;
    }

    public static SearchOperation getSimpleOperation(final String value) {
        return switch (value) {
            case "=" -> EQUAL;
            case ">=" -> GRATER_THAN_OR_EQ;
            case "<=" -> LESS_THAN_OR_EQ;
            case "~" -> LIKE;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
