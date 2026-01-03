package com.java.ecom.dto.response;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Integer id;
    private String prodName;
    private double price;
    private String category;
    private Boolean isAvailable;
}

