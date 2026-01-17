package com.java.ecom.controller;

import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.entity.Refund;
import com.java.ecom.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    // USER (COD) → Submit Bank / UPI Details
    @PutMapping("/{orderId}/bank-details/{userId}")
    public ResponseEntity<String> submitBankDetails(
            @PathVariable Long orderId,
            @PathVariable UUID userId,
            @RequestBody RefundBankDetailsDto dto) {

        refundService.submitRefundBankDetails(orderId, userId, dto);
        return ResponseEntity.ok("Refund initiated successfully");
    }

    //Get refund status
    @GetMapping("/{orderId}")
    public ResponseEntity<Refund> getRefund(@PathVariable Long orderId) {
        return ResponseEntity.ok(refundService.getRefundByOrderId(orderId));
    }
}
