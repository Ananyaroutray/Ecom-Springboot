package com.java.ecom.pattern.returnStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.OrderReturn;

public interface ReturnStrategy {
    void processReturn(OrderReturn returnEntity, Order order);
}
