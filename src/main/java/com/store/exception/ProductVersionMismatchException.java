package com.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductVersionMismatchException extends RuntimeException {
    
    public ProductVersionMismatchException(String message) {
        super(message);
    }
} 