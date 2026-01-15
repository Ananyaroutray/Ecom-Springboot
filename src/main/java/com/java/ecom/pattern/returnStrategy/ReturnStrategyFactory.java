package com.java.ecom.pattern.returnStrategy;

import com.java.ecom.enums.PaymentMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReturnStrategyFactory {

    private final OnlineReturnStrategy onlineReturnStrategy;
    private final CodReturnStrategy codReturnStrategy;

    public ReturnStrategy getStrategy(PaymentMode mode) {

        return switch (mode) {
            case ONLINE -> onlineReturnStrategy;
            case CASH_ON_DELIVERY -> codReturnStrategy;
        };
    }
}
