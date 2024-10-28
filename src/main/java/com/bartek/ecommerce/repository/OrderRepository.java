package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}