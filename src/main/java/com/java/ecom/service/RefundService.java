package com.java.ecom.service;

import com.java.ecom.dto.request.RefundBankDetailsDto;

import java.util.UUID;

public interface RefundService {
    void submitRefundBankDetails(Long orderId, UUID userId, RefundBankDetailsDto dto);
}
