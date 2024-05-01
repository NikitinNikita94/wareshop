package team.mediasoft.wareshop.exchanger.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import team.mediasoft.wareshop.exchanger.model.CurrencyProvider;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
@Order(1)
@AllArgsConstructor
public class OncePerRequestFilter implements Filter {

    public static final String JSESSIONID = "JSESSIONID";
    public static final String HEADER_RATE_NAME = "currency";
    public static final String RUB_CURRENCY = "RUB";
    private CurrencyProvider currencyProvider;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!hasJsessionid(request)) {
            currencyProvider.setRate(RUB_CURRENCY);
        } else {
            String currency = request.getHeader(HEADER_RATE_NAME);
            if (currency == null) {
                currency = currencyProvider.getRate() == null ? RUB_CURRENCY : currencyProvider.getRate();
            }
            log.info("Header: {}", currency);
            currencyProvider.setRate(currency);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static boolean hasJsessionid(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(JSESSIONID));
    }
}
