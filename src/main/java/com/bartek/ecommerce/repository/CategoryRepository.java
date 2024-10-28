package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}