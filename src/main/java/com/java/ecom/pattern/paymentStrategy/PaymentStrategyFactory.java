package com.java.ecom.pattern.paymentStrategy;

import com.java.ecom.enums.PaymentMode;
import com.java.ecom.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategy getStrategy(PaymentMode mode) {

        if (mode == null) {
            throw new BadRequestException("Payment mode is not set for this order");
        }

        return switch (mode) {
            case ONLINE -> strategies.get("onlinePaymentStrategy");
            case CASH_ON_DELIVERY -> strategies.get("cashOnDeliveryPaymentStrategy");
        };
    }
}
