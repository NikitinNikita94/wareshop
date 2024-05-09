package team.mediasoft.wareshop.data.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.mediasoft.wareshop.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Query(value = "SELECT * FROM product FOR UPDATE ", nativeQuery = true)
    List<Product> findAllProducts(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(UUID id);

    Boolean existsByAmountIsGreaterThanEqual(Integer quantity);

    Boolean existsByAmountIsLessThan(Integer quantity);

    @Query(value = "select p.is_available from product p where p.id = :productId", nativeQuery = true)
    Boolean getIsAvailableById(@Param("productId") UUID productId);

    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.amount = p.amount - :deductAmount where p.id = :productId")
    void updateAmountProductMinus(@Param("productId") UUID productId, @Param("deductAmount") Integer deductAmount);

    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.amount = p.amount + :deductAmount where p.id = :productId")
    void updateAmountProductPlus(@Param("productId") UUID productId, @Param("deductAmount") Integer deductAmount);

    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.isAvailable = false where p.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);
}
