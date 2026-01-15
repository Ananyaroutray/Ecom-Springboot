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
public class CashOnDeliveryPaymentStrategy implements PaymentStrategy {

    private final PaymentRepo paymentRepo;

    @Override
    public void processPayment(Order order, boolean success) {

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setUserId(order.getUserId().toString());
        payment.setPaymentMode(PaymentMode.CASH_ON_DELIVERY);
        payment.setPaymentTime(LocalDateTime.now());

        // COD never fails at payment time
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        paymentRepo.save(payment);

        // Order is CONFIRMED, not PAID
        order.setStatus(OrderStatus.CONFIRMED);
    }
}
