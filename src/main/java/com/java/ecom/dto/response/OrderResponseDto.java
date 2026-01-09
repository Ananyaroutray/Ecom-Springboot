package com.java.ecom.dto.response;

import com.java.ecom.enums.OrderStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderResponseDto {
    private Long orderId;
    private UUID userId;
    private Double totalAmount;
    private OrderStatus status;
    private List<OrderItemResponseDto> items;
}
