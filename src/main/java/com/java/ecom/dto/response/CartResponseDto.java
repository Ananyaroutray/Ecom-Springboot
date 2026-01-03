package com.java.ecom.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDto {
    private Long cartId;
    private Integer userId;
    private Double totalAmount;
    private List<CartItemResponseDto> items;
}

