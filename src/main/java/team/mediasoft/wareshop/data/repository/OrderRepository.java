package team.mediasoft.wareshop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.mediasoft.wareshop.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
