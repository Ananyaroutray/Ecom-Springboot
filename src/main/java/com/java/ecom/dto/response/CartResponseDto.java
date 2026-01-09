package com.java.ecom.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartResponseDto {
    private Long cartId;
    private UUID userId;
    private Double totalAmount;
    private List<CartItemResponseDto> items;
}

