package com.example.orders.domain.service.impl;

import com.example.orders.client.ProductsClient;
import com.example.orders.client.dto.ProductSummary;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderItem;
import com.example.orders.domain.service.OrderService;
import com.example.orders.dto.OrderItemRequest;
import com.example.orders.dto.OrderItemResponse;
import com.example.orders.dto.OrderRequest;
import com.example.orders.dto.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductsClient products;
    private final Map<String, Order> storage = new ConcurrentHashMap<>();

    public OrderServiceImpl(ProductsClient products) {
        this.products = products;
    }

    @Override
    public OrderResponse create(OrderRequest req) {
        Order order = new Order();
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest it : req.items()) {
            ProductSummary p = products.getById(it.productId());
            items.add(new OrderItem(p.id(), p.name(), p.price(), it.quantity()));
        }
        order.setItems(items);
        storage.put(order.getId(), order);
        return toResponse(order);
    }

    @Override
    public OrderResponse get(String id) {
        Order o = storage.get(id);
        if (o == null) throw new IllegalArgumentException("Order not found: " + id);
        return toResponse(o);
    }

    @Override
    public List<OrderResponse> list() {
        return storage.values().stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order o){
        return new OrderResponse(
                o.getId(),
                o.getItems().stream().map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getProductName(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getLineTotal()
                )).toList(),
                o.getTotal(),
                o.getCreatedAt()
        );
    }
}
