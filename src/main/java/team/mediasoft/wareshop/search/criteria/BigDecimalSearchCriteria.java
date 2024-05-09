package team.mediasoft.wareshop.search.criteria;

import lombok.Data;
import team.mediasoft.wareshop.search.predicate.BigDecimalPredicateStrategyImpl;
import team.mediasoft.wareshop.search.predicate.PredicateStrategy;

import java.math.BigDecimal;

@Data
public class BigDecimalSearchCriteria implements SearchCriteria<BigDecimal> {

    private static final PredicateStrategy<BigDecimal> PREDICATE = new BigDecimalPredicateStrategyImpl();
    private final String field;
    private final SearchOperation operation;
    private final BigDecimal value;

    @Override
    public PredicateStrategy<BigDecimal> getPredicateStrategy() {
        return PREDICATE;
    }
}
