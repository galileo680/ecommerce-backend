package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Order;
import com.bartek.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}