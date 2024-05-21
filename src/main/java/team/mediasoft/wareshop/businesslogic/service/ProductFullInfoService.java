package team.mediasoft.wareshop.businesslogic.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import team.mediasoft.wareshop.businesslogic.service.account.CustomerAccountService;
import team.mediasoft.wareshop.businesslogic.service.crm.CustomerCrmService;
import team.mediasoft.wareshop.data.repository.OrderRepository;
import team.mediasoft.wareshop.entity.Order;
import team.mediasoft.wareshop.entity.dto.customer.CustomerInfo;
import team.mediasoft.wareshop.entity.dto.order.OrderInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.Pair.of;

@Service
@RequiredArgsConstructor
public class ProductFullInfoService {

    private final OrderRepository orderRepository;
    private final CustomerAccountService accountService;
    private final CustomerCrmService crmService;


    @SneakyThrows
    public Map<UUID, List<OrderInfo>> getOrderInfo() {
        final List<Order> allOrderedByStatus = orderRepository.getAllOrderedByStatus();
        final List<String> allStatusCustomerList = allOrderedByStatus.stream()
                .map(order -> order.getCustomer().getLogin())
                .toList();

        Map<String, String> accountMap = accountService.getNonBlockAccountCustomersByLogins(allStatusCustomerList).get();
        Map<String, String> innMap = crmService.getNonBlockCrmCustomersByLogins(allStatusCustomerList).get();

        return allOrderedByStatus.stream()
                .flatMap(order -> order.getOrderItems().stream()
                        .map(oi -> of(oi.getPk().getProductId(),
                                new OrderInfo(
                                        order.getId(),
                                        new CustomerInfo(order.getCustomer().getId(),
                                                accountMap.get(order.getCustomer().getLogin()),
                                                order.getCustomer().getEmail(),
                                                innMap.get(order.getCustomer().getLogin())),
                                        order.getStatus(),
                                        order.getDeliveryAddress(),
                                        oi.getQuantity()
                                )))
                ).collect(
                        groupingBy(
                                Pair::getFirst,
                                mapping(Pair::getSecond, toList())
                        ));

    }
}
