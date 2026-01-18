package com.java.ecom.controller;

import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.entity.Refund;
import com.java.ecom.enums.PaymentMode;
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

    // USER → Submit bank / UPI details (only for COD)
    @PutMapping("/{orderId}/bank-details/{userId}")
    public ResponseEntity<String> submitBankDetails(
            @PathVariable Long orderId,
            @PathVariable UUID userId,
            @RequestBody(required = false) RefundBankDetailsDto dto) {

        refundService.submitRefundBankDetails(orderId, userId, dto);
        return ResponseEntity.ok("Refund processing updated");
    }

    // USER / ADMIN → Get refund status
    @GetMapping("/{orderId}")
    public ResponseEntity<Refund> getRefund(@PathVariable Long orderId) {
        return ResponseEntity.ok(refundService.getRefundByOrderId(orderId));
    }
}
