package team.mediasoft.wareshop.exchanger.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import team.mediasoft.wareshop.exchanger.model.Currency;
import team.mediasoft.wareshop.exchanger.model.CurrencyProvider;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {
    private CurrencyProvider currencyProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String currency = request.getHeader("currency");
        Optional.ofNullable(currency)
                .map(Currency::getCurrency)
                .ifPresent(currencyProvider::setCurrency);

        filterChain.doFilter(request, response);
    }
}
