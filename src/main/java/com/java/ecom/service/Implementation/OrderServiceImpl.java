package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.CheckoutRequestDto;
import com.java.ecom.dto.response.OrderItemResponseDto;
import com.java.ecom.dto.response.OrderResponseDto;
import com.java.ecom.entity.*;
import com.java.ecom.enums.OrderStatus;
import com.java.ecom.exception.BadRequestException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.repository.*;
import com.java.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepo cartRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;

    @Override
    @Transactional
    public OrderResponseDto checkout(Integer userId, CheckoutRequestDto dto) {

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
