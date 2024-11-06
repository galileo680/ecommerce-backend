package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.entity.CartItem;
import com.bartek.ecommerce.entity.OrderItem;
import com.bartek.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}