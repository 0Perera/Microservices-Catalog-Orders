package com.example.orders.domain.service;

import com.example.orders.dto.OrderRequest;
import com.example.orders.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest req);
    OrderResponse get(String id);
    List<OrderResponse> list();
}
