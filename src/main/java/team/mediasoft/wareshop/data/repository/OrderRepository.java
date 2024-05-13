package team.mediasoft.wareshop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.mediasoft.wareshop.entity.Order;
import team.mediasoft.wareshop.entity.dto.order.OrderProductDtoInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT distinct new team.mediasoft.wareshop.entity.dto.order.OrderProductDtoInfo(" +
           "oi.pk.orderId ," +
           " p.name," +
           " oi.quantity," +
           " oi.price)" +
           "from Order o " +
           "inner join OrderItem oi on :orderId = oi.pk.orderId" +
           " join Product p on oi.pk.productId = p.id")
    List<OrderProductDtoInfo> getAllProductsByOrderId(@Param("orderId") UUID orderId);

    @Query("""
                select o from Order o
                left join fetch o.orderItems ps
                left join fetch ps.product p
                where o.id = :orderId
           """)
    Optional<Order> findByIdFetchOrderedProducts(UUID orderId);
}
