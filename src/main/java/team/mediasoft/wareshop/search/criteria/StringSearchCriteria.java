package team.mediasoft.wareshop.search.criteria;

import lombok.Data;
import team.mediasoft.wareshop.search.predicate.PredicateStrategy;
import team.mediasoft.wareshop.search.predicate.StringPredicateStrategyImp;

@Data
public class StringSearchCriteria implements SearchCriteria<String> {

    private static final PredicateStrategy<String> PREDICATE = new StringPredicateStrategyImp();
    private final String field;
    private final SearchOperation operation;
    private final String value;

    @Override
    public PredicateStrategy<String> getPredicateStrategy() {
        return PREDICATE;
    }
}
