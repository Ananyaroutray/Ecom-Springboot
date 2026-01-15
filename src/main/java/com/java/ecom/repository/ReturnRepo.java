package com.java.ecom.repository;

import com.java.ecom.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnRepo extends JpaRepository<Return, Long> {

    Optional<Return> findByOrderId(Long orderId);
}
