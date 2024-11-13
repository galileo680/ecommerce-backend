package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.CartDto;
import com.bartek.ecommerce.entity.Cart;

public interface CartService {

    Cart getCartByUserId(Long userId);
    CartDto addItemToCart(Long userId, Long productId, Integer quantity);
    CartDto updateCartItem(Long userId, Long cartItemId, int quantity);
    void removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
