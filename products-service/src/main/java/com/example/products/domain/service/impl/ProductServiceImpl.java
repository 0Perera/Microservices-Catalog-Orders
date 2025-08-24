package com.example.products.domain.service.impl;

import com.example.products.domain.model.Product;
import com.example.products.domain.repository.ProductRepository;
import com.example.products.domain.service.ProductService;
import com.example.products.dto.ProductRequest;
import com.example.products.dto.ProductResponse;
import com.example.products.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product p = ProductMapper.toEntity(req);
        return ProductMapper.toResponse(repo.save(p));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return repo.findAll().stream().map(ProductMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return repo.findById(id).map(ProductMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        ProductMapper.copy(req, p);
        return ProductMapper.toResponse(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
