package com.store.dto;

import java.math.BigDecimal;
 
public record CreateProductRequest(
    String name,
    BigDecimal price
) {} 