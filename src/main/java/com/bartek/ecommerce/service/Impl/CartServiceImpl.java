package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.CartDto;
import com.bartek.ecommerce.entity.Cart;
import com.bartek.ecommerce.entity.CartItem;
import com.bartek.ecommerce.entity.Product;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.exception.IllegalAccessException;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.CartMapper;
import com.bartek.ecommerce.repository.CartItemRepository;
import com.bartek.ecommerce.repository.CartRepository;
import com.bartek.ecommerce.repository.ProductRepository;
import com.bartek.ecommerce.repository.UserRepository;
import com.bartek.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Cart getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));

        return cart;
    }

    @Override
    public CartDto addItemToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Cart cart = getCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    newItem.setUnitPrice(product.getPrice());
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setUnitPrice(product.getPrice());

        cartItemRepository.save(cartItem);

        updateCartTotal(cart);

        return CartMapper.toCartDto(cart);
    }

    @Override
    public CartDto updateCartItem(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalAccessException("You are not authorized to modify this cart item");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        updateCartTotal(cartItem.getCart());

        return CartMapper.toCartDto(cartItem.getCart());
    }

    @Override
    public void removeItemFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalAccessException("You are not authorized to remove this cart item");
        }
        cartItemRepository.delete(cartItem);

        updateCartTotal(cartItem.getCart());
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    //Helper methods
    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalAmount(BigDecimal.ZERO);
        return cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }
}
