package team.mediasoft.wareshop.search.criteria;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import team.mediasoft.wareshop.search.predicate.PredicateStrategy;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        visible = true,
        include = JsonTypeInfo.As.PROPERTY,
        property = "field"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringSearchCriteria.class, name = "name"),
        @JsonSubTypes.Type(value = LocalDateSearchCriteria.class, name = "createAt"),
        @JsonSubTypes.Type(value = StringSearchCriteria.class, name = "description"),
        @JsonSubTypes.Type(value = BigDecimalSearchCriteria.class, name = "price")
})
public interface SearchCriteria<T> {

    String getField();

    SearchOperation getOperation();

    T getValue();

    PredicateStrategy<T> getPredicateStrategy();
}
