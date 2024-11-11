package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.CartDto;
import com.bartek.ecommerce.dto.CartItemDto;
import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.entity.CartItem;
import com.bartek.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public static CartDto toCartDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setTotalAmount(cart.getTotalAmount());

        List<CartItemDto> cartItemDTOs = cart.getCartItems().stream()
                .map(CartMapper::toCartItemDto)
                .collect(Collectors.toList());
        cartDto.setCartItems(cartItemDTOs);

        return cartDto;
    }

    public static Cart toCart(CartDto cartDto) {
        if (cartDto == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setId(cartDto.getId());
        cart.setTotalAmount(cartDto.getTotalAmount());

        List<CartItem> cartItems = cartDto.getCartItems().stream()
                .map(CartMapper::toCartItem)
                .collect(Collectors.toList());
        cart.setCartItems(cartItems);

        for (CartItem cartItem : cartItems) {
            cartItem.setCart(cart);
        }

        return cart;
    }

    public static CartItemDto toCartItemDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setProductId(cartItem.getProduct().getId());
        cartItemDto.setProductName(cartItem.getProduct().getName());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setUnitPrice(cartItem.getUnitPrice());
        cartItemDto.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        return cartItemDto;
    }

    public static CartItem toCartItem(CartItemDto cartItemDto) {
        if (cartItemDto == null) {
            return null;
        }

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemDto.getId());
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItem.setUnitPrice(cartItemDto.getUnitPrice());

        Product product = new Product();
        product.setId(cartItemDto.getProductId());
        cartItem.setProduct(product);

        return cartItem;
    }
}