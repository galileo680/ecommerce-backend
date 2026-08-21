package com.galileo.ecommerce.catalog.application;

import com.galileo.ecommerce.catalog.domain.Product;
import com.galileo.ecommerce.catalog.domain.ProductStatus;
import com.galileo.ecommerce.catalog.infrastructure.ProductRepository;
import com.galileo.ecommerce.catalog.infrastructure.ProductSpecifications;
import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository products;

    public Page<Product> search(ProductSearchCriteria criteria, Pageable pageable) {
        List<Specification<Product>> parts = new ArrayList<>();
        parts.add(ProductSpecifications.hasStatus(ProductStatus.ACTIVE));
        if (criteria.categoryId() != null) {
            parts.add(ProductSpecifications.inCategory(criteria.categoryId()));
        }
        if (criteria.minPrice() != null) {
            parts.add(ProductSpecifications.priceAtLeast(criteria.minPrice()));
        }
        if (criteria.maxPrice() != null) {
            parts.add(ProductSpecifications.priceAtMost(criteria.maxPrice()));
        }
        if (criteria.phrase() != null && !criteria.phrase().isBlank()) {
            parts.add(ProductSpecifications.nameContains(criteria.phrase()));
        }
        return products.findAll(Specification.allOf(parts), pageable);
    }

    public Product getActive(UUID id) {
        return products.findById(id)
            .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("product %s not found".formatted(id)));
    }
}
