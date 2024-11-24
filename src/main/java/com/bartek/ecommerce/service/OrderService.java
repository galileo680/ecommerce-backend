package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.OrderDto;
import com.bartek.ecommerce.dto.OrderRequest;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(Long userId, OrderRequest orderRequest);
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto getOrderById(Long orderId, Long userId);
}
