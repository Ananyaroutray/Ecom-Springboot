package com.java.ecom.pattern.returnStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Return;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.PaymentMode;
import com.java.ecom.enums.ReturnStatus;
import com.java.ecom.pattern.refundStrategy.RefundStrategy;
import com.java.ecom.pattern.refundStrategy.RefundStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineReturnStrategy implements ReturnStrategy {

    private final RefundStrategyFactory refundStrategyFactory;

    @Override
    public void processReturn(Return returnEntity, Order order) {

        order.setStatus(OrderStatus.RETURNED);
        returnEntity.setReturnStatus(ReturnStatus.COMPLETED);

        // Refund will be triggered AFTER approval
        RefundStrategy refundStrategy =
                refundStrategyFactory.getStrategy(PaymentMode.ONLINE);

        refundStrategy.processRefund(order);
    }
}
