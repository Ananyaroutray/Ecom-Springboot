package com.java.ecom.service;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.response.OrderResponseDto;

import java.util.UUID;

public interface OrderService {
    OrderResponseDto checkout(UUID userId, CheckoutRequestDto dto);
}
