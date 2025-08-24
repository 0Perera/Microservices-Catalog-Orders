package com.example.products.mapper;

import com.example.products.domain.model.Product;
import com.example.products.dto.ProductRequest;
import com.example.products.dto.ProductResponse;

public class ProductMapper {
    public static Product toEntity(ProductRequest r){
        return new Product(null, r.name(), r.price(), r.description());
    }
    public static void copy(ProductRequest r, Product p){
        p.setName(r.name());
        p.setPrice(r.price());
        p.setDescription(r.description());
    }
    public static ProductResponse toResponse(Product p){
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getDescription());
    }
}
