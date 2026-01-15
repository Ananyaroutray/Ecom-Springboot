package com.java.ecom.repository;

import com.java.ecom.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepo extends JpaRepository<Refund, Long> {
    Optional<Refund> findByOrderId(Long orderId);
}
