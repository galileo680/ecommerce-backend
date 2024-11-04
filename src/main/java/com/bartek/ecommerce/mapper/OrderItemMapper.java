package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.OrderItemDto;
import com.bartek.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    /*public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .status(orderItem.getStatus().name())
                .createdAt(orderItem.getCreatedAt())
                .build();

        return orderItemDto;
    }*/
}
