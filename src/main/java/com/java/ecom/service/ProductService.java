package com.java.ecom.service;

import com.java.ecom.dto.request.ProductRequestDto;
import com.java.ecom.dto.response.ProductResponseDto;
import com.java.ecom.entity.Product;
import com.java.ecom.entity.User;

import java.util.List;

public interface ProductService {

    List<ProductResponseDto> getAllProducts();
    ProductResponseDto addProduct(ProductRequestDto dto);
    ProductResponseDto updateProduct(Integer id, ProductRequestDto dto);
    void deleteProduct(Integer id);

}
