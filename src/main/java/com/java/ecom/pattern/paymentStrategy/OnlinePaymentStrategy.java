package com.java.ecom.pattern.paymentStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Payment;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.PaymentMode;
import com.java.ecom.enums.PaymentStatus;
import com.java.ecom.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OnlinePaymentStrategy implements PaymentStrategy {


    private final PaymentRepo paymentRepo;

    @Override
    public void processPayment(Order order, boolean success) {

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setUserId(order.getUserId().toString());
        payment.setPaymentMode(PaymentMode.ONLINE);
        payment.setPaymentTime(LocalDateTime.now());

        if (success) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN_" + System.currentTimeMillis());
            order.setStatus(OrderStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        paymentRepo.save(payment);
    }
}
