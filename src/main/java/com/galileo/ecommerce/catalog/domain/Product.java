package com.galileo.ecommerce.catalog.domain;

import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product", schema = "catalog")
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "price_amount", precision = 19, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", length = 3)
    private String priceCurrency;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attributes = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Product() {
    }

    private Product(UUID id, Sku sku, String name, String description, Money price, UUID categoryId,
                    Map<String, String> attributes) {
        this.id = id;
        this.sku = sku.value();
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
        this.status = ProductStatus.DRAFT;
        setPrice(price);
    }

    public static Product create(Sku sku, String name, String description, Money price, UUID categoryId,
                                 Map<String, String> attributes) {
        return new Product(UUID.randomUUID(), sku, name, description, price, categoryId, attributes);
    }

    public void update(String name, String description, Money price, UUID categoryId,
                       Map<String, String> attributes) {
        requireNotArchived();
        if (status == ProductStatus.ACTIVE && price == null) {
            throw new BusinessRuleException("an active product must keep a price");
        }
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
        setPrice(price);
    }

    public void activate() {
        requireNotArchived();
        if (status == ProductStatus.ACTIVE) {
            throw new BusinessRuleException("product is already active");
        }
        if (priceAmount == null) {
            throw new BusinessRuleException("a product cannot be activated without a price");
        }
        this.status = ProductStatus.ACTIVE;
    }

    public void archive() {
        this.status = ProductStatus.ARCHIVED;
    }

    private void requireNotArchived() {
        if (status == ProductStatus.ARCHIVED) {
            throw new BusinessRuleException("an archived product cannot be changed");
        }
    }

    private void setPrice(Money price) {
        if (price == null) {
            this.priceAmount = null;
            this.priceCurrency = null;
            return;
        }
        if (price.isNegative()) {
            throw new BusinessRuleException("a product price cannot be negative");
        }
        this.priceAmount = price.amount();
        this.priceCurrency = price.currency();
    }

    public UUID getId() {
        return id;
    }

    public Sku getSku() {
        return new Sku(sku);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return priceAmount == null ? null : new Money(priceAmount, priceCurrency);
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public Map<String, String> getAttributes() {
        return Map.copyOf(attributes);
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
