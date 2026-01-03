package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.ProductRequestDto;
import com.java.ecom.dto.response.ProductResponseDto;
import com.java.ecom.entity.Product;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.mapper.ProductMapper;
import com.java.ecom.repository.ProductRepo;
import com.java.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .filter(Product::getIsActive)
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto addProduct(ProductRequestDto dto) {

        Product product = productMapper.toEntity(dto);

        product.setIsActive(true);
        product.setIsAvailable(dto.getStock() > 0);

        Product saved = productRepo.save(product);
        return productMapper.toResponseDto(saved);
    }

    @Override
    public ProductResponseDto updateProduct(Integer id, ProductRequestDto dto) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Product not found for id: " + id));

        productMapper.toUpdateEntityFromDto(dto, product);

        // update availability based on stock
        product.setIsAvailable(product.getStock() > 0);

        return productMapper.toResponseDto(product);
    }

    @Override
    public void deleteProduct(Integer id) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Product not found for id: " + id));

        // SOFT DELETE
        product.setIsActive(false);
    }
}
