package com.java.ecom.pattern.refundStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Payment;
import com.java.ecom.entity.Refund;
import com.java.ecom.enums.PaymentMode;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.repository.PaymentRepo;
import com.java.ecom.repository.RefundRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OnlineRefundStrategy implements RefundStrategy {

    private final RefundRepo refundRepo;
    private final PaymentRepo paymentRepo;

    @Override
    public void processRefund(Order order) {

        Payment payment = paymentRepo.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setPaymentMode(PaymentMode.ONLINE);
        refund.setRefundStatus(RefundStatus.SUCCESS);
        refund.setRefundReference("REF_" + System.currentTimeMillis());
        refund.setRefundTime(LocalDateTime.now());

        refundRepo.save(refund);
    }

}
