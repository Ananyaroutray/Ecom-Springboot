package com.java.ecom.repository;

import com.java.ecom.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Integer userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

