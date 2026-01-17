package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.entity.Order;
import com.java.ecom.entity.Refund;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.exception.BadRequestException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.repository.OrderRepo;
import com.java.ecom.repository.RefundRepo;
import com.java.ecom.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundRepo refundRepo;
    private final OrderRepo orderRepo;

    @Transactional
    @Override
    public void submitRefundBankDetails(Long orderId, UUID userId, RefundBankDetailsDto dto) {

        Refund refund = refundRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Refund not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized");
        }

        if (refund.getRefundStatus() != RefundStatus.BANK_DETAILS_REQUIRED) {
            throw new BadRequestException("Bank details not required");
        }

        refund.setUpiId(dto.getUpiId());
        refund.setBankAccount(dto.getBankAccount());
        refund.setIfscCode(dto.getIfscCode());

        refund.setRefundStatus(RefundStatus.INITIATED);
        refund.setRefundReference("REF_" + System.currentTimeMillis());
        refund.setRefundTime(LocalDateTime.now());

        refundRepo.save(refund);
    }

    @Override
    public Refund getRefundByOrderId(Long orderId) {

        return refundRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Refund not found for orderId: " + orderId));
    }
}
