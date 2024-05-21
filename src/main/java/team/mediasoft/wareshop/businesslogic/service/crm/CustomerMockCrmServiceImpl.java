package team.mediasoft.wareshop.businesslogic.service.crm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@Primary
@ConditionalOnProperty(name = "app.rest.currency-service.mock.enabled", havingValue = "true")
public class CustomerMockCrmServiceImpl implements CustomerCrmService {
    @Override
    public Map<String, String> getBlockCrmCustomersByLogins(List<String> logins) {
        return Map.of("UserOleg", "123456789012", "UserVlad", "123456789012");
    }

    @Override
    public CompletableFuture<Map<String, String>> getNonBlockCrmCustomersByLogins(List<String> logins) {
        return CompletableFuture.completedFuture(Map.of("UserOleg", "123456789012", "UserVlad", "123456789012"));
    }
}
