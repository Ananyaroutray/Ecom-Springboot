package com.java.ecom.dto.response;

import lombok.Data;

@Data
public class OrderItemResponseDto {
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subTotal;
}
