package com.store.dto;

import java.math.BigDecimal;
 
public record UpdateProductPriceRequest(
    BigDecimal price
) {} 