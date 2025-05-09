package com.store.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
 
public record CreateProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(max = 80, message = "Product name must be less than 80 characters")
    String name,
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or higher")
    BigDecimal price
) {} 