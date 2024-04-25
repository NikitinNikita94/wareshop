package team.mediasoft.wareshop.search.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;
import team.mediasoft.wareshop.search.criteria.SearchOperation;

import java.util.Objects;

@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private final SearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return switch (Objects.requireNonNull(SearchOperation.getSimpleOperation(
                String.valueOf(searchCriteria.getOperation())))) {
            case EQUAL ->
                    searchCriteria.getPredicateStrategy().getEqPredicate(root.get(searchCriteria.getField()), searchCriteria.getValue(), cb);
            case GRATER_THAN_OR_EQ ->
                    searchCriteria.getPredicateStrategy().getLeftLimitPredicate(root.get(searchCriteria.getField()), searchCriteria.getValue(), cb);
            case LESS_THAN_OR_EQ ->
                    searchCriteria.getPredicateStrategy().getRightLimitPredicate(root.get(searchCriteria.getField()), searchCriteria.getValue(), cb);
            case LIKE ->
                    searchCriteria.getPredicateStrategy().getLikePredicate(root.get(searchCriteria.getField()), searchCriteria.getValue(), cb);
        };
    }
}
