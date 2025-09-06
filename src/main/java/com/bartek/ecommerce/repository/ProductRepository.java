package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCategoryIdAndArchivedFalse(Long categoryId);
    List<Product> findByArchivedFalse();
    List<Product> findByNameContainingOrDescriptionContainingAndArchivedFalse(String name, String description);

    //ADMIN
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
    List<Product> findByArchivedTrue();
}