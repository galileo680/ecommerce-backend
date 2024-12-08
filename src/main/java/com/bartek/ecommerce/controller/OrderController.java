package com.bartek.ecommerce.controller;

import com.bartek.ecommerce.dto.OrderDto;
import com.bartek.ecommerce.dto.OrderRequest;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid OrderRequest orderRequest
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();

        OrderDto orderDto = orderService.placeOrder(user.getId(), orderRequest);

        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(
            @AuthenticationPrincipal User user
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        List<OrderDto> ordersDto = orderService.getOrdersByUserId(user.getId());

        return ResponseEntity.ok(ordersDto);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId
    ) {
        //Long userId = SecurityUtil.getCurrentUserId();
        OrderDto orderDto = orderService.getOrderById(orderId, user.getId());

        return ResponseEntity.ok(orderDto);
    }
}