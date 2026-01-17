package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.dto.request.ReturnRequestDto;
import com.java.ecom.entity.Order;
import com.java.ecom.entity.OrderReturn;
import com.java.ecom.entity.Payment;
import com.java.ecom.entity.Refund;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.enums.ReturnStatus;
import com.java.ecom.exception.BadRequestException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.pattern.returnStrategy.ReturnStrategy;
import com.java.ecom.pattern.returnStrategy.ReturnStrategyFactory;
import com.java.ecom.repository.OrderRepo;
import com.java.ecom.repository.PaymentRepo;
import com.java.ecom.repository.RefundRepo;
import com.java.ecom.repository.ReturnRepo;
import com.java.ecom.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {


    private final OrderRepo orderRepo;
    private final ReturnRepo returnRepo;
    private final PaymentRepo paymentRepo;
    private final ReturnStrategyFactory returnStrategyFactory;


    @Transactional
    @Override
    public void requestReturn(Long orderId, UUID userId, ReturnRequestDto dto) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized return request");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Return allowed only after delivery");
        }

        // 7-day return window
        if (order.getDeliveredAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Return window expired");
        }

        OrderReturn returnEntity = new OrderReturn();
        returnEntity.setOrderId(orderId);
        returnEntity.setUserId(userId.toString());
        returnEntity.setReturnStatus(ReturnStatus.REQUESTED);
        returnEntity.setReason(dto.getReason());
        returnEntity.setRequestedAt(LocalDateTime.now());

        returnRepo.save(returnEntity);

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepo.save(order);
    }

    @Transactional
    @Override
    public void approveReturn(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderReturn returnEntity = returnRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Return request not found"));

        if (returnEntity.getReturnStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException("Return already processed");
        }

        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        ReturnStrategy strategy =
                returnStrategyFactory.getStrategy(payment.getPaymentMode());

        returnEntity.setReturnStatus(ReturnStatus.APPROVED);
        returnEntity.setApprovedAt(LocalDateTime.now());

        strategy.processReturn(returnEntity, order);

        returnRepo.save(returnEntity);
        orderRepo.save(order);
    }



}
