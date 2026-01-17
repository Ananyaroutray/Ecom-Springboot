package com.java.ecom.service;

import com.java.ecom.dto.request.PaymentRequestDto;

import java.util.UUID;

public interface PaymentService {
    void processPayment(Long orderId, UUID userId, PaymentRequestDto dto);
}
