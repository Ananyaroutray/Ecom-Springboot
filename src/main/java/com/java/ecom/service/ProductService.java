package com.java.ecom.service;

import com.java.ecom.entity.Product;
import com.java.ecom.entity.User;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product addProduct(Product product);
    Product updateProduct(int id, Product product);
    void deleteProduct(int id);

}
