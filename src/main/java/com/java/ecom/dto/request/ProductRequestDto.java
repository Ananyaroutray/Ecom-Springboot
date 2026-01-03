package com.java.ecom.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequestDto {
    private String prodName;
    private String category;
    @Min(0)
    private Integer stock;
    @Positive
    private double price;
}

