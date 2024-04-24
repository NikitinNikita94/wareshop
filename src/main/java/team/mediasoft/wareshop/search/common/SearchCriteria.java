package team.mediasoft.wareshop.search.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    private String fieldKey;
    private Object value;
    private String operation;
}
