package com.store.dto;

import com.store.model.Product;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    Integer version,
    Instant createdAt,
    Instant updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getVersion(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
} 