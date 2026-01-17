package com.java.ecom.service;

import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.dto.request.ReturnRequestDto;

import java.util.UUID;

public interface ReturnService {
    void requestReturn(Long orderId, UUID userId, ReturnRequestDto dto);
    void approveReturn(Long orderId);
}
