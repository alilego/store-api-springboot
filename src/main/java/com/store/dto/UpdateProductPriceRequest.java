package com.store.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
 
public record UpdateProductPriceRequest(
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or higher")
    BigDecimal price,
    
    @Min(value = 0, message = "Version must be 0 or higher")
    Integer version
) {} 