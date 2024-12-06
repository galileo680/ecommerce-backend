package com.bartek.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
