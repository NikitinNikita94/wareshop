package team.mediasoft.wareshop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.mediasoft.wareshop.entity.Order;
import team.mediasoft.wareshop.entity.OrderItem;
import team.mediasoft.wareshop.entity.dto.order.OrderProductDtoInfo;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT distinct new team.mediasoft.wareshop.entity.dto.order.OrderProductDtoInfo(" +
           "oi.pk.product.id ," +
           " p.name," +
           " oi.quantity," +
           " oi.price)" +
           "from Order o " +
           "inner join OrderItem oi on :orderId = oi.pk.order.id" +
           " join Product p on oi.pk.product.id = p.id")
    List<OrderProductDtoInfo> getAllProductsByOrderId(@Param("orderId") UUID orderId);

    @Query("select oi from OrderItem oi where oi.pk.order = :order")
    List<OrderItem> findByOrderItemByOrder(@Param("order") Order order);
}
