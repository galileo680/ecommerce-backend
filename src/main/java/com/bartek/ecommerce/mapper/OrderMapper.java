package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.OrderDto;
import com.bartek.ecommerce.dto.OrderItemDto;
import com.bartek.ecommerce.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public static OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setShippingAddress(AddressMapper.toAddressDto(order.getShippingAddress()));
        dto.setBillingAddress(AddressMapper.toAddressDto(order.getBillingAddress()));

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(OrderItemMapper::toOrderItemDto)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDtos);

        //dto.setPayment(PaymentMapper.toPaymentDto(order.getPayment()));

        return dto;
    }
}
