package team.mediasoft.wareshop.search.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;

public class BigDecimalPredicateStrategyImpl implements PredicateStrategy<BigDecimal> {
    @Override
    public Predicate getEqPredicate(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate getLeftLimitPredicate(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getRightLimitPredicate(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getLikePredicate(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value.multiply(BigDecimal.valueOf(0.9))),
                cb.lessThanOrEqualTo(expression, value.multiply(BigDecimal.valueOf(1.1)))
        );
    }
}
