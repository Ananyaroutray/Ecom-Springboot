package com.java.ecom.entity;

import com.java.ecom.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // snapshot of address
    private String deliveryAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
}

