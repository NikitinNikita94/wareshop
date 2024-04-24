package team.mediasoft.wareshop.entity.response;

import org.springframework.http.HttpStatus;

public record APIResponse(
        Object data,
        String message,
        HttpStatus responseCode
) {
}
