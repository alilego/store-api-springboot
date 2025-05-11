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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Validated
@Tag(name = "Products", description = "Product management endpoints")
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Creates a new product with the given name and price. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
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

    @Operation(summary = "Get a product by ID", description = "Retrieves a product by its ID. Accessible by both USER and ADMIN roles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "ID of the product to retrieve") 
            @PathVariable Long id) {
        logger.info("GET /api/products/{} - Fetching product", id);
        
        Product product = productService.getProductById(id);
        ProductResponse response = ProductResponse.from(product);
        
        logger.info("GET /api/products/{} - Product found: name={}, price={}", 
            id, response.name(), response.price());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update product price", description = "Updates the price of an existing product. Supports optimistic locking via version field. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Price updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "Version mismatch")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductPrice(
            @Parameter(description = "ID of the product to update")
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

    @Operation(summary = "List all products", description = "Retrieves a paginated list of products. Supports sorting and pagination. Accessible by both USER and ADMIN roles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") 
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
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

    @Operation(summary = "Delete a product", description = "Soft deletes a product by ID. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete")
            @PathVariable Long id) {
        logger.info("DELETE /api/products/{} - Soft deleting product", id);
        productService.softDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }
} 