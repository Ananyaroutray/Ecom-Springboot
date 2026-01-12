package com.java.ecom.pattern;

import com.java.ecom.entity.Order;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class OnlinePaymentStrategy implements PaymentStrategy{


    @Override
    public void processPayment(Order order, boolean success) {

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Online payment not allowed now");
        }

        if (!success) {
            throw new BadRequestException("Online payment failed");
        }

        order.setStatus(OrderStatus.PAID);
    }
}
