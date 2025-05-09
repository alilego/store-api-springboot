package com.store.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
 
public record UpdateProductPriceRequest(
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or higher")
    BigDecimal price
) {} 