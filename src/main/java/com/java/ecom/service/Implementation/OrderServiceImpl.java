package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.request.PaymentRequestDto;
import com.java.ecom.dto.request.RefundBankDetailsDto;
import com.java.ecom.dto.request.ReturnRequestDto;
import com.java.ecom.dto.response.OrderItemResponseDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.entity.*;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.enums.PaymentMode;
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
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus current = order.getStatus();

        // STATUS TRANSITION VALIDATION
        switch (newStatus) {

            case CONFIRMED -> {
                if (current != OrderStatus.PLACED && current != OrderStatus.PAID) {
                    throw new BadRequestException("Invalid transition");
                }
            }

            case SHIPPED -> {
                if (order.getPaymentMode() == PaymentMode.ONLINE &&
                        current != OrderStatus.PAID) {
                    throw new BadRequestException("Online payment required before shipping");
                }
                if (order.getPaymentMode() == PaymentMode.CASH_ON_DELIVERY &&
                        current != OrderStatus.CONFIRMED) {
                    throw new BadRequestException("Order must be confirmed first");
                }
            }

            case DELIVERED -> {
                if (order.getPaymentMode() == PaymentMode.ONLINE &&
                        current != OrderStatus.SHIPPED) {
                    throw new BadRequestException("Invalid delivery state");
                }
                if (order.getPaymentMode() == PaymentMode.CASH_ON_DELIVERY &&
                        current != OrderStatus.SHIPPED) {
                    throw new BadRequestException("Order must be shipped first");
                }

                // COD → mark PAID at delivery
                if (order.getPaymentMode() == PaymentMode.CASH_ON_DELIVERY) {
                    order.setStatus(OrderStatus.PAID);
                }
            }

            default -> throw new BadRequestException("Unsupported status transition");
        }

        order.setStatus(newStatus);
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
