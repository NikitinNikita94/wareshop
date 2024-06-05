package team.mediasoft.wareshop.integration.crm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CustomerCrmService {
    Map<String, String> getBlockCrmCustomersByLogins(List<String> logins);

    CompletableFuture<Map<String, String>> getNonBlockCrmCustomersByLogins(List<String> logins);
}
