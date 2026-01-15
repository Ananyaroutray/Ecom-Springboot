package com.java.ecom.service;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.dto.request.ReturnRequestDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto checkout(UUID userId, CheckoutRequestDto dto);
    void updateOrderStatus(Long orderId, OrderStatus status);
    List<OrderResponseDto> getOrderHistory(UUID userId);
    void cancelOrder(Long orderId, UUID userId);
    void processPayment(Long orderId, UUID userId, PaymentRequestDto dto);
    void requestReturn(Long orderId, UUID userId, ReturnRequestDto dto);
    void approveReturn(Long orderId);
}
