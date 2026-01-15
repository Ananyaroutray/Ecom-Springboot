package com.java.ecom.pattern.returnStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Return;

public interface ReturnStrategy {
    void processReturn(Return returnEntity, Order order);
}
