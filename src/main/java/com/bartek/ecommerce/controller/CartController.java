package com.bartek.ecommerce.controller;

import com.bartek.ecommerce.dto.CartDto;
import com.bartek.ecommerce.dto.CartItemDto;
import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.mapper.CartMapper;
import com.bartek.ecommerce.service.CartService;
import com.bartek.ecommerce.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        Cart cart = cartService.getCartByUserId(userId);
        CartDto cartDto = CartMapper.toCartDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(
            @RequestBody @Valid CartItemDto cartItemDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        CartDto cartDto = cartService.addItemToCart(userId, cartItemDto.getProductId(), cartItemDto.getQuantity());

        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable Long itemId, @RequestBody @Valid CartItemDto cartItemDTO
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        CartDto cartDto = cartService.updateCartItem(userId, itemId, cartItemDTO.getQuantity());

        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long itemId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.removeItemFromCart(userId, itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}
