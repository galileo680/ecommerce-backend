package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.OrderItemDto;
import com.bartek.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {

    public static OrderItemDto toOrderItemDto(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        //dto.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return dto;
    }
}
