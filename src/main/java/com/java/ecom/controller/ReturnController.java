package com.java.ecom.controller;

import com.java.ecom.dto.request.ReturnRequestDto;
import com.java.ecom.service.OrderService;
import com.java.ecom.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService requestReturn;

    // 1️⃣ USER → Request Return
    @PutMapping("/{orderId}/return/{userId}")
    public ResponseEntity<String> requestReturn(
            @PathVariable Long orderId,
            @PathVariable UUID userId,
            @RequestBody ReturnRequestDto dto) {

        requestReturn.requestReturn(orderId, userId, dto);
        return ResponseEntity.ok("Return requested successfully");
    }

    // 2️⃣ ADMIN → Approve Return
    @PutMapping("/admin/{orderId}/return/approve")
    public ResponseEntity<String> approveReturn(
            @PathVariable Long orderId) {

        requestReturn.approveReturn(orderId);
        return ResponseEntity.ok("Return approved successfully");
    }
}
