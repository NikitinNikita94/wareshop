package team.mediasoft.wareshop.search.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface PredicateStrategy<T> {

    Predicate getEqPredicate(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getLeftLimitPredicate(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getRightLimitPredicate(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getLikePredicate(Expression<T> expression, T value, CriteriaBuilder cb);
}
