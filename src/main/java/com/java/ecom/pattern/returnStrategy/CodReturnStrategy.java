package com.java.ecom.pattern.returnStrategy;

import com.java.ecom.entity.Order;
import com.java.ecom.entity.Refund;
import com.java.ecom.entity.Return;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.PaymentMode;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.repository.RefundRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodReturnStrategy implements ReturnStrategy {

    private final RefundRepo refundRepo;

    @Override
    public void processReturn(Return returnEntity, Order order) {

        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setPaymentMode(PaymentMode.CASH_ON_DELIVERY);
        refund.setRefundStatus(RefundStatus.BANK_DETAILS_REQUIRED);

        refundRepo.save(refund);

        order.setStatus(OrderStatus.RETURNED);
    }
}
