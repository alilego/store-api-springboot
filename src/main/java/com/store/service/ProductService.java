package com.store.service;

import com.store.exception.ProductNotFoundException;
import com.store.exception.ProductVersionMismatchException;
import com.store.model.Product;
import com.store.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        logger.info("Adding new product: name={}, price={}", product.getName(), product.getPrice());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        logger.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public Product updatePrice(Long id, BigDecimal newPrice, Integer expectedVersion) {
        logger.info("Updating price for product id: {} to: {}, expected version: {}", id, newPrice, expectedVersion);
        Product product = getProductById(id);
        
        if (expectedVersion != null && !expectedVersion.equals(product.getVersion())) {
            throw new ProductVersionMismatchException(
                String.format("Your version of product with id=%d is outdated. Your version: %d. Current version: %d", 
                    id, expectedVersion, product.getVersion()));
        }
        
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        logger.info("Fetching products with pagination: page={}, size={}, sort={}", 
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            pageable.getSort());
        return productRepository.findAll(pageable);
    }
} 