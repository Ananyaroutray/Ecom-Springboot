package com.java.ecom.repository;

import com.java.ecom.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepo extends JpaRepository<Refund, Long> {
}
