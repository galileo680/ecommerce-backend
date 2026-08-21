package com.galileo.ecommerce.catalog.infrastructure;

import com.galileo.ecommerce.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsBySlug(String slug);

    boolean existsByParentId(UUID parentId);
}
