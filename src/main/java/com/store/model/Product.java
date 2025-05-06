package com.store.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Product(
    Long id,
    String name,
    BigDecimal price,
    Integer version,
    Instant createdAt,
    Instant updatedAt
) {} 