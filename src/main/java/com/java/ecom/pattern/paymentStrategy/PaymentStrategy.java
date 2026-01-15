package com.java.ecom.pattern.paymentStrategy;

import com.java.ecom.entity.Order;

public interface PaymentStrategy {
    void processPayment(Order order, boolean success);
}
