package com.java.ecom.pattern.refundStrategy;

import com.java.ecom.enums.PaymentMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundStrategyFactory {

    private final OnlineRefundStrategy onlineRefundStrategy;
    private final CodRefundStrategy codRefundStrategy;

    public RefundStrategy getStrategy(PaymentMode mode) {

        return switch (mode) {
            case ONLINE -> onlineRefundStrategy;
            case CASH_ON_DELIVERY -> codRefundStrategy;
        };
    }
}
