package com.java.ecom.entity;

import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status;

    // snapshot of address
    private String deliveryAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
}

