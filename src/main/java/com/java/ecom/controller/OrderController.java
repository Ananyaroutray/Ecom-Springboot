package com.java.ecom.controller;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CHECKOUT--PLACE ORDER
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderResponseDto> checkout(
            @PathVariable UUID userId,
            @RequestBody CheckoutRequestDto dto) {

        OrderResponseDto response = orderService.checkout(userId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ADMIN / SYSTEM
    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated");
    }

    //ORDER HISTORY
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    //CANCEL ORDER
    @PutMapping("/{orderId}/cancel/{userId}")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            @PathVariable UUID userId) {

        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    //PAYMENT
    @PutMapping("/{orderId}/pay/{userId}")
    public ResponseEntity<String> processPayment(
            @PathVariable Long orderId,
            @PathVariable UUID userId,
            @RequestBody PaymentRequestDto dto
    ) {
        orderService.processPayment(orderId, userId, dto);
        return ResponseEntity.ok("Payment processed successfully");
    }

}
