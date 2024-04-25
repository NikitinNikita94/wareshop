package team.mediasoft.wareshop.search.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class StringPredicateStrategyImp implements PredicateStrategy<String> {
    @Override
    public Predicate getEqPredicate(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate getLeftLimitPredicate(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getRightLimitPredicate(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getLikePredicate(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, "%" + value + "%");
    }
}
