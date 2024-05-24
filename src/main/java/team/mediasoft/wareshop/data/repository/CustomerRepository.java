package team.mediasoft.wareshop.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.mediasoft.wareshop.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
