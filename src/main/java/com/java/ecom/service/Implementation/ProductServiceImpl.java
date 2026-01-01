package com.java.ecom.service.Implementation;

import com.java.ecom.entity.Product;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.repository.ProductRepo;
import com.java.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {


    private final ProductRepo productRepo;


    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(int id, Product product) {
        Product existingProducts = productRepo.findById(id).orElseThrow(()-> new NotFoundException("Product not found for id: "+id));
        existingProducts.setProdName(product.getProdName());
        existingProducts.setCategory(product.getCategory());
        existingProducts.setPrice(product.getPrice());
        existingProducts.setIsAvailable(product.getIsAvailable());
        return productRepo.save(existingProducts);
    }

    @Override
    public void deleteProduct(int id) {
        Product existingProducts = productRepo.findById(id).orElseThrow(()-> new NotFoundException("Product not found for id: "+id));
        productRepo.delete(existingProducts);
    }
}
