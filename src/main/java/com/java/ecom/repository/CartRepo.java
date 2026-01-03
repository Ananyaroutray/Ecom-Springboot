package com.java.ecom.repository;

import com.java.ecom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserId(Integer userId);

}
