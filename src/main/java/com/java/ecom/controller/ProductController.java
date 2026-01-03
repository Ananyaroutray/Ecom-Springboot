package com.java.ecom.controller;

import com.java.ecom.dto.request.ProductRequestDto;
import com.java.ecom.dto.response.ProductResponseDto;
import com.java.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> addProduct(
            @RequestBody ProductRequestDto dto) {

        return new ResponseEntity<>(
                productService.addProduct(dto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequestDto dto) {

        return ResponseEntity.ok(
                productService.updateProduct(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

}
