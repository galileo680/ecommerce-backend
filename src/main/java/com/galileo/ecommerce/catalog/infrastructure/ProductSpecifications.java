package com.galileo.ecommerce.catalog.infrastructure;

import com.galileo.ecommerce.catalog.domain.Product;
import com.galileo.ecommerce.catalog.domain.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Product> inCategory(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("categoryId"), categoryId);
    }

    public static Specification<Product> priceAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("priceAmount"), min);
    }

    public static Specification<Product> priceAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("priceAmount"), max);
    }

    public static Specification<Product> nameContains(String phrase) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + phrase.trim().toLowerCase() + "%");
    }
}
