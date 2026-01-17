package com.java.ecom.controller;

import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class PaymentController {

    private final PaymentService paymentService;

    //PAYMENT
    @PutMapping("/{orderId}/pay/{userId}")
    public ResponseEntity<String> processPayment(
            @PathVariable Long orderId,
            @PathVariable UUID userId,
            @RequestBody PaymentRequestDto dto
    ) {
        paymentService.processPayment(orderId, userId, dto);
        return ResponseEntity.ok("Payment processed successfully");
    }
}
