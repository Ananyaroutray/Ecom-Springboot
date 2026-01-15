package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.dto.request.ReturnRequestDto;
import com.java.ecom.dto.response.OrderItemResponseDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.entity.*;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.RefundStatus;
import com.java.ecom.enums.ReturnStatus;
import com.java.ecom.exception.BadRequestException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.pattern.paymentStrategy.PaymentStrategy;
import com.java.ecom.pattern.paymentStrategy.PaymentStrategyFactory;
import com.java.ecom.pattern.refundStrategy.RefundStrategy;
import com.java.ecom.pattern.refundStrategy.RefundStrategyFactory;
import com.java.ecom.pattern.returnStrategy.ReturnStrategy;
import com.java.ecom.pattern.returnStrategy.ReturnStrategyFactory;
import com.java.ecom.repository.*;
import com.java.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepo cartRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final PaymentRepo paymentRepo;
    private final RefundStrategyFactory refundStrategyFactory;
    private final ReturnRepo returnRepo;
    private final ReturnStrategyFactory returnStrategyFactory;
    private final RefundRepo refundRepo;

    @Override
    @Transactional
    public OrderResponseDto checkout(UUID userId, CheckoutRequestDto dto) {

        // 1️ Fetch cart
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart has no items");
        }

        // 2️ Fetch user & address
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepo
                .findByIdAndUser_Id(dto.getAddressId(), userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        // 3️ Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());
        order.setDeliveryAddress(
                address.getStreet() + ", " +
                        address.getCity() + ", " +
                        address.getState() + " - " +
                        address.getPincode()
        );

        List<OrderItem> orderItems = new ArrayList<>();

        // 4️ Process each cart item
        for (CartItem cartItem : cart.getItems()) {

            Product product = productRepo.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (cartItem.getQuantity() > product.getStock()) {
                throw new RuntimeException("Insufficient stock for product: "
                        + product.getProdName());
            }

            // reduce stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setIsAvailable(product.getStock() > 0);

            // create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubTotal(cartItem.getSubTotal());

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(cart.getTotalAmount());

        // 5️ Save order
        Order savedOrder = orderRepo.save(order);

        // 6️ Clear cart
        cart.getItems().clear();
        cart.setTotalAmount(0.0);

        // 7️ Build response
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Admin should NOT mark PAID
        if (status == OrderStatus.PAID) {
            throw new BadRequestException("Payment must be done via payment API");
        }

        // Invalid transitions
        if (order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order cannot be updated");
        }

        order.setStatus(status);
        orderRepo.save(order);
    }

    @Override
    public List<OrderResponseDto> getOrderHistory(UUID userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Order> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, UUID userId) {

        // 1️ Fetch order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // 2️ Ownership check
        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot cancel someone else's order");
        }

        // 3️ Status validation
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order already cancelled");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order cannot be cancelled now");
        }

        if (order.getStatus() != OrderStatus.PAID &&
                order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        // 4️ Restore stock
        for (OrderItem item : order.getItems()) {

            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setStock(product.getStock() + item.getQuantity());
            product.setIsAvailable(true);
        }

        // 5️ Refund ONLY if PAID
        if (order.getStatus() == OrderStatus.PAID) {
            Payment payment = paymentRepo.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            RefundStrategy refundStrategy =
                    refundStrategyFactory.getStrategy(payment.getPaymentMode());
            refundStrategy.processRefund(order);
        }

        // 6️ Cancel order
        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

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

        Return returnEntity = new Return();
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

        Return returnEntity = returnRepo.findByOrderId(orderId)
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

    @Transactional
    public void submitRefundBankDetails(Long orderId, UUID userId, RefundBankDetailsDto dto) {

        Refund refund = refundRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Refund not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized");
        }

        if (refund.getRefundStatus() != RefundStatus.BANK_DETAILS_REQUIRED) {
            throw new BadRequestException("Bank details not required");
        }

        refund.setUpiId(dto.getUpiId());
        refund.setBankAccount(dto.getBankAccount());
        refund.setIfscCode(dto.getIfscCode());

        refund.setRefundStatus(RefundStatus.INITIATED);
        refund.setRefundReference("REF_" + System.currentTimeMillis());
        refund.setRefundTime(LocalDateTime.now());

        refundRepo.save(refund);
    }



    private OrderResponseDto mapToResponse(Order order) {

        List<OrderItemResponseDto> items = order.getItems()
                .stream()
                .map(item -> {
                    OrderItemResponseDto dto = new OrderItemResponseDto();
                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    dto.setSubTotal(item.getSubTotal());
                    return dto;
                })
                .toList();

        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setItems(items);

        return response;
    }

}
