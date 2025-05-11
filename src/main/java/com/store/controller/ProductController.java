package com.store.controller;

// Spring Framework imports
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// Jakarta EE imports
import jakarta.validation.Valid;

// Logging imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Application imports
import com.store.dto.CreateProductRequest;
import com.store.dto.ProductResponse;
import com.store.dto.UpdateProductPriceRequest;
import com.store.model.Product;
import com.store.service.ProductService;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        logger.info("POST /api/products - Creating product: name={}, price={}", request.name(), request.price());
        
        Product product = new Product(request.name(), request.price());
        Product savedProduct = productService.addProduct(product);
        ProductResponse response = ProductResponse.from(savedProduct);
        
        logger.info("POST /api/products - Product created successfully: id={}, name={}, price={}", 
            response.id(), response.name(), response.price());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        logger.info("GET /api/products/{} - Fetching product", id);
        
        Product product = productService.getProductById(id);
        ProductResponse response = ProductResponse.from(product);
        
        logger.info("GET /api/products/{} - Product found: name={}, price={}", 
            id, response.name(), response.price());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductPrice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductPriceRequest request) {
        logger.info("PUT /api/products/{} - Updating price to {}, version: {}", 
            id, request.price(), request.version());
        
        Product updatedProduct = productService.updatePrice(id, request.price(), request.version());
        ProductResponse response = ProductResponse.from(updatedProduct);
        
        logger.info("PUT /api/products/{} - Price updated successfully: new price={}, new version={}", 
            id, response.price(), response.version());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        logger.info("GET /api/products - Fetching products with pagination: page={}, size={}, sortBy={}, direction={}", 
            page, size, sortBy, direction);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Product> products = productService.getAllProducts(pageRequest);
        Page<ProductResponse> response = products.map(ProductResponse::from);
        
        logger.info("GET /api/products - Found {} products (page {} of {})", 
            response.getNumberOfElements(), 
            response.getNumber() + 1, 
            response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /api/products/{} - Soft deleting product", id);
        productService.softDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }
} 