package com.java.ecom.pattern.refundStrategy;

import com.java.ecom.entity.Order;

public interface RefundStrategy {
    void processRefund(Order order);
}
