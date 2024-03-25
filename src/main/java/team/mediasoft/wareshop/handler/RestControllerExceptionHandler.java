package team.mediasoft.wareshop.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "java/team/mediasoft/wareshop/controller/rest")
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {
}
