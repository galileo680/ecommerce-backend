package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.dto.OrderDto;
import com.bartek.ecommerce.dto.OrderItemDto;
import com.bartek.ecommerce.dto.OrderRequest;
import com.bartek.ecommerce.entity.*;
import com.bartek.ecommerce.enums.OrderStatus;
import com.bartek.ecommerce.exception.IllegalAccessException;
import com.bartek.ecommerce.exception.InsufficientStockException;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.OrderMapper;
import com.bartek.ecommerce.repository.CartRepository;
import com.bartek.ecommerce.repository.OrderRepository;
import com.bartek.ecommerce.repository.ProductRepository;
import com.bartek.ecommerce.repository.UserRepository;
import com.bartek.ecommerce.service.CartService;
import com.bartek.ecommerce.service.OrderService;
import com.bartek.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final EmailService emailService;

    @Override
    public OrderDto placeOrder(Long userId, OrderRequest orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order with an empty cart");
        }

        validateCartItems(cart);

        Order order = createOrder(user, cart, orderRequest);

        Payment payment = paymentService.processPayment(order, orderRequest.getPaymentDetails());
        order.setPayment(payment);

        updateInventory(order);

        orderRepository.save(order);

        //cart.getCartItems().clear();
        //cartService.clearCart(userId);
        //cartRepository.save(cart);

        emailService.sendOrderConfirmationEmail(
                user.getEmail(),
                user.getFirstname(),
                order.getId(),
                order.getOrderItems().stream().map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setProductName(item.getProduct().getName());
                    dto.setQuantity(item.getQuantity());
                    dto.setUnitPrice(item.getUnitPrice());
                    return dto;
                }).collect(Collectors.toList()),
                order.getTotalAmount(),
                AddressDto.builder()
                        .street(order.getShippingStreet())
                        .city(order.getShippingCity())
                        .state(order.getShippingState())
                        .postalCode(order.getShippingPostalCode())
                        .country(order.getShippingCountry())
                        .build()
        );

        return OrderMapper.toOrderDto(order);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(OrderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalAccessException("You are not authorized to view this order");
        }
        return OrderMapper.toOrderDto(order);
    }

    //Helper methods
    private void validateCartItems(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
        }
    }

    private Order createOrder(User user, Cart cart, OrderRequest orderRequest) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalAmount());

        order.setShippingStreet(orderRequest.getShippingAddress().getStreet());
        order.setShippingCity(orderRequest.getShippingAddress().getCity());
        order.setShippingState(orderRequest.getShippingAddress().getState());
        order.setShippingPostalCode(orderRequest.getShippingAddress().getPostalCode());
        order.setShippingCountry(orderRequest.getShippingAddress().getCountry());


        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        return order;
    }

    private void updateInventory(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int updatedQuantity = product.getQuantity() - item.getQuantity();
            if (updatedQuantity < 0) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(updatedQuantity);
            productRepository.save(product);
        }
    }


}
