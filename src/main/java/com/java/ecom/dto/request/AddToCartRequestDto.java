package com.java.ecom.dto.request;

import lombok.Data;

@Data
public class AddToCartRequestDto {
    private Integer productId;
    private Integer quantity;
}
