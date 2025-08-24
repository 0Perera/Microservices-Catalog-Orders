package com.example.orders.client.dto;

import java.math.BigDecimal;

public record ProductSummary(Long id, String name, BigDecimal price, String description) {}
