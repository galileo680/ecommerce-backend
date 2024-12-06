package com.bartek.ecommerce.controller;

import com.bartek.ecommerce.dto.CartDto;
import com.bartek.ecommerce.dto.CartItemDto;
import com.bartek.ecommerce.dto.CartItemUpdateRequest;
import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.mapper.CartMapper;
import com.bartek.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(
            @AuthenticationPrincipal User user
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();

        Cart cart = cartService.getCartByUserId(user.getId());
        CartDto cartDto = CartMapper.toCartDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CartItemDto cartItemDto
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        CartDto cartDto = cartService.addItemToCart(user.getId(), cartItemDto.getProductId(), cartItemDto.getQuantity());

        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestBody @Valid CartItemUpdateRequest cartItemUpdateRequest
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        CartDto cartDto = cartService.updateCartItem(user.getId(), itemId, cartItemUpdateRequest.getQuantity());

        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        cartService.removeItemFromCart(user.getId(), itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal User user
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        cartService.clearCart(user.getId());

        return ResponseEntity.noContent().build();
    }
}
