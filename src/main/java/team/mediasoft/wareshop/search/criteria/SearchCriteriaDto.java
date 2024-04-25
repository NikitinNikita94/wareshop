package team.mediasoft.wareshop.search.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDto<T> {
    private List<SearchCriteria<T>> searchCriteriaList;
}
