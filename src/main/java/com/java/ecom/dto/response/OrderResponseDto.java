package com.java.ecom.dto.response;

import com.java.ecom.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDto {
    private Long orderId;
    private Integer userId;
    private Double totalAmount;
    private OrderStatus status;
    private List<OrderItemResponseDto> items;
}
