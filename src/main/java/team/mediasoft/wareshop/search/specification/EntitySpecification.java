package team.mediasoft.wareshop.search.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import team.mediasoft.wareshop.search.common.SearchCriteria;

@RequiredArgsConstructor
public class EntitySpecification implements Specification {

    private final SearchCriteria searchCriteria;


    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
        return null;
    }
}
