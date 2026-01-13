package com.java.ecom.repository;

import com.java.ecom.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment,Long> {
    Optional<Payment> findByOrderId(Long orderId);
}
