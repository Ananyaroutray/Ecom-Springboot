package com.java.ecom.dto.response;

import lombok.Data;

@Data
public class CartItemResponseDto {
    private Long id;
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subTotal;
}

