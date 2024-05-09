package team.mediasoft.wareshop.search.specification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;
import team.mediasoft.wareshop.search.criteria.SearchOperation;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductSpecification {

    private final List<SearchCriteria> searchCriteriaList;

    public Specification<Product> buildSpecification() {
        List<Specification<Product>> list = searchCriteriaList.stream()
                .map(this::getSpecification)
                .toList();
        return Specification.allOf(list);
    }

    private Specification<Product> getSpecification(SearchCriteria searchCriteria) {
        return (root, query, cb) -> switch (Objects.requireNonNull(SearchOperation.getSimpleOperation(
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
