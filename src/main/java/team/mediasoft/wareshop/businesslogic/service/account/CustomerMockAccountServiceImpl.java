package team.mediasoft.wareshop.businesslogic.service.account;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@Primary
@ConditionalOnProperty(name = "app.rest.currency-service.mock.enabled", havingValue = "true")
public class CustomerMockAccountServiceImpl implements CustomerAccountService {
    @Override
    public Map<String, String> getBlockAccountCustomersByLogins(List<String> logins) {
        return Map.of("UserOleg", "12345678", "UserVlad", "46364362");
    }

    @Override
    public CompletableFuture<Map<String, String>> getNonBlockAccountCustomersByLogins(List<String> logins) {
        return CompletableFuture.completedFuture(Map.of("UserOleg", "12345678", "UserVlad", "46364362"));
    }
}
