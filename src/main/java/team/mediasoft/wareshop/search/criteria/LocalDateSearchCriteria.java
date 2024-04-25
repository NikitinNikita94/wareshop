package team.mediasoft.wareshop.search.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import team.mediasoft.wareshop.search.predicate.LocalDatePredicateStrategyImpl;
import team.mediasoft.wareshop.search.predicate.PredicateStrategy;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LocalDateSearchCriteria implements SearchCriteria<LocalDate> {

    private static final PredicateStrategy<LocalDate> PREDICATE = new LocalDatePredicateStrategyImpl();
    private String field;
    private SearchOperation operation;
    private LocalDate value;

    @Override
    public PredicateStrategy<LocalDate> getPredicateStrategy() {
        return PREDICATE;
    }
}
