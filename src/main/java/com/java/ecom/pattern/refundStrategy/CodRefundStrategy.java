package com.java.ecom.pattern.refundStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Refund;
import com.java.ecom.enums.PaymentMode;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.repository.RefundRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CodRefundStrategy implements RefundStrategy {

    private final RefundRepo refundRepo;

    @Override
    public void processRefund(Order order) {

        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setPaymentMode(PaymentMode.CASH_ON_DELIVERY);
        refund.setRefundStatus(RefundStatus.NOT_APPLICABLE);
        refund.setRefundTime(LocalDateTime.now());

        refundRepo.save(refund);
    }
}

