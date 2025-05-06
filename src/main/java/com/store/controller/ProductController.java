package com.store.controller;

import com.store.dto.CreateProductRequest;
import com.store.dto.ProductResponse;
import com.store.dto.UpdateProductPriceRequest;
import com.store.model.Product;
import com.store.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        logger.info("POST /api/products - Creating product: name={}, price={}", request.name(), request.price());
        
        Product product = new Product(request.name(), request.price());
        Product savedProduct = productService.addProduct(product);
        
        return ResponseEntity.status(201).body(ProductResponse.from(savedProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        logger.info("GET /api/products/{} - Fetching product", id);
        
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductPrice(
            @PathVariable Long id,
            @RequestBody UpdateProductPriceRequest request) {
        logger.info("PUT /api/products/{} - Updating price to {}", id, request.price());
        
        Product updatedProduct = productService.updatePrice(id, request.price());
        return ResponseEntity.ok(ProductResponse.from(updatedProduct));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        logger.info("GET /api/products - Fetching all products");
        
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }
} 