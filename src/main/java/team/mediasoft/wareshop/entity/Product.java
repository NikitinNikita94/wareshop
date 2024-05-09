package team.mediasoft.wareshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.mediasoft.wareshop.entity.enumeration.ProductCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product implements Serializable {

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

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "last_amount_up")
    private LocalDateTime lastAmountUp;

    @Column(name = "create_at", nullable = false)
    private LocalDate createAt;

    @OneToMany(mappedBy = "pk.product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.id).append(" ");
        builder.append(this.name).append(" ");
        builder.append(this.vendorCode).append(" ");
        builder.append(this.description).append(" ");
        builder.append(this.category).append(" ");
        builder.append(this.price).append(" ");
        builder.append(this.amount).append(" ");
        builder.append(this.lastAmountUp).append(" ");
        builder.append(this.createAt);
        return builder.toString();
    }
}
