package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.entity.CartItem;
import com.bartek.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}