package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.entity.Order;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.exception.BadRequestException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.pattern.paymentStrategy.PaymentStrategy;
import com.java.ecom.pattern.paymentStrategy.PaymentStrategyFactory;
import com.java.ecom.repository.OrderRepo;
import com.java.ecom.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepo orderRepo;
    private final PaymentStrategyFactory paymentStrategyFactory;

    @Override
    @Transactional
    public void processPayment(Long orderId, UUID userId, PaymentRequestDto dto) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized payment attempt");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Payment not allowed for this order status");
        }

        PaymentStrategy strategy =
                paymentStrategyFactory.getStrategy(dto.getPaymentMode());

        strategy.processPayment(order, dto.getSuccess());

        orderRepo.save(order); // only order saved here
    }
}
