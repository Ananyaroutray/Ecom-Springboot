package com.java.ecom.pattern;

import com.java.ecom.entity.Order;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryPaymentStrategy implements PaymentStrategy{

    @Override
    public void processPayment(Order order, boolean success) {

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException(
                    "COD payment allowed only after delivery"
            );
        }

        if (!success) {
            throw new BadRequestException("COD payment failed");
        }

        order.setStatus(OrderStatus.PAID);
    }
}
