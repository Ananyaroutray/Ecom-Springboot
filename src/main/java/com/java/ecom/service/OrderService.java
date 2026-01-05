package com.java.ecom.service;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.response.OrderResponseDto;

public interface OrderService {
    OrderResponseDto checkout(Integer userId, CheckoutRequestDto dto);
}
