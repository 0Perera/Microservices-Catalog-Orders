package com.example.orders.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        List<OrderItemResponse> items,
        BigDecimal total,
        OffsetDateTime createdAt
) {}
