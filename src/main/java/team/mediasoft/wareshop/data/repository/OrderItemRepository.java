package team.mediasoft.wareshop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.mediasoft.wareshop.entity.OrderItem;
import team.mediasoft.wareshop.entity.embeddable.OrderItemPk;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk> {
}
