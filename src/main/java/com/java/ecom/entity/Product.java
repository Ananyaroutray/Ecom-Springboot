package com.java.ecom.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String prodName;
    private double price;
    private String category;
    private Integer stock;
    @Column(nullable = false)
    private Boolean isAvailable = true;
    @Column(nullable = false)
    private Boolean isActive = true;


}
