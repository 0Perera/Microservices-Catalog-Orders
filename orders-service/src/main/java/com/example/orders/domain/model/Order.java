package com.example.orders.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String id;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Order() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.total = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
