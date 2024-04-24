package team.mediasoft.wareshop.search.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDto {
    private List<SearchCriteria> searchCriteriaList;
}
