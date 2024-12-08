package com.bartek.ecommerce.entity;

import com.bartek.ecommerce.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderStatus status;
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //Address stuff
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private LocalDateTime orderDate;
}