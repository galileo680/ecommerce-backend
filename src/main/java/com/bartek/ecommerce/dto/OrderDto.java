package com.bartek.ecommerce.dto;

import com.bartek.ecommerce.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private AddressDto shippingAddress;
    private List<OrderItemDto> orderItems;
    //private PaymentDto payment;
}