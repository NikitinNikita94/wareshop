package team.mediasoft.wareshop.businesslogic.service.account;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CustomerAccountService {
    Map<String, String> getBlockAccountCustomersByLogins(List<String> logins);

    CompletableFuture<Map<String, String>> getNonBlockAccountCustomersByLogins(List<String> logins);
}
