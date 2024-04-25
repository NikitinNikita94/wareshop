package team.mediasoft.wareshop.search.specification;

import org.springframework.data.jpa.domain.Specification;
import team.mediasoft.wareshop.entity.Product;
import team.mediasoft.wareshop.search.criteria.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationBuilder {

    private final List<SearchCriteria> params;

    public ProductSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final ProductSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<Product> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Product> result = new ProductSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = Specification.where(result).and(new ProductSpecification(criteria));
        }
        return result;
    }

}