package com.store.controller;

import com.store.dto.CreateProductRequest;
import com.store.dto.UpdateProductPriceRequest;
import com.store.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request) {
        logger.info("POST /api/products - Creating product: name={}, price={}", request.name(), request.price());


        // Placeholder response
        Product product = new Product(
            1L,
            request.name(),
            request.price(),
            1,
            Instant.now(),
            Instant.now()
        );
        
        return ResponseEntity.status(201).body(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        logger.info("GET /api/products/{} - Fetching product", id);

        // Placeholder response
        Product product = new Product(
            id,
            "Sample Product",
            new BigDecimal("99.99"),
            1,
            Instant.now(),
            Instant.now()
        );
        
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProductPrice(
            @PathVariable Long id,
            @RequestBody UpdateProductPriceRequest request) {
        logger.info("PUT /api/products/{} - Updating price to {}", id, request.price());

        // Placeholder response
        Product product = new Product(
            id,
            "Sample Product",
            request.price(),
            2,
            Instant.now(),
            Instant.now()
        );
        
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("GET /api/products - Fetching all products");

        // Placeholder response
        List<Product> products = Arrays.asList(
            new Product(
                1L,
                "Premium Widget",
                new BigDecimal("99.99"),
                1,
                Instant.now(),
                Instant.now()
            ),
            new Product(
                2L,
                "Basic Widget",
                new BigDecimal("49.99"),
                1,
                Instant.now(),
                Instant.now()
            )
        );
        
        return ResponseEntity.ok(products);
    }
} 