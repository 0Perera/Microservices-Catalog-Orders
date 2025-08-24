package com.example.orders.client;

import com.example.orders.client.dto.ProductSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products-service")
public interface ProductsClient {
    @GetMapping("/products/{id}")
    ProductSummary getById(@PathVariable("id") Long id);
}
