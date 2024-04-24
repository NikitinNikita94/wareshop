package team.mediasoft.wareshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "vendor_code", nullable = false)
    private Integer vendorCode;
    @Column(name = "description", nullable = false)
    private String description;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory category;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "amount", nullable = false)
    private Integer amount;
    @Column(name = "last_amount_up")
    private LocalDateTime lastUpdateAmount;
    @Column(name = "create_at", nullable = false)
    private LocalDate createAt;

}
