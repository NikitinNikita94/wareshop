package team.mediasoft.wareshop.search.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;

public class LocalDatePredicateStrategyImpl implements PredicateStrategy<LocalDate> {
    @Override
    public Predicate getEqPredicate(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate getLeftLimitPredicate(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getRightLimitPredicate(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getLikePredicate(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.between(expression, value.minusDays(10), value.plusDays(10));
    }
}
