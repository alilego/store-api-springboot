package com.store.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductVersionMismatchException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductVersionMismatchException.class);
    
    public ProductVersionMismatchException(String message) {
        super(message);
        logger.warn("Product version mismatch: {}", message);
    }
} 