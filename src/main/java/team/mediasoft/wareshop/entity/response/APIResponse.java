package team.mediasoft.wareshop.entity.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record APIResponse(
        Object data,
        String message,
        HttpStatus responseCode
) {
}
