package com.store.service;

import com.store.config.CacheConfig;
import com.store.exception.ProductNotFoundException;
import com.store.exception.ProductVersionMismatchException;
import com.store.model.Product;
import com.store.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CachePut(value = CacheConfig.PRODUCTS_CACHE, key = "#result.id")
    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        logger.info("Adding new product: name={}, price={}", product.getName(), product.getPrice());
        return productRepository.save(product);
    }

    @Cacheable(value = CacheConfig.PRODUCTS_CACHE, key = "#id", unless = "#result.deleted")
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        logger.info("Cache miss - Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @CachePut(value = CacheConfig.PRODUCTS_CACHE, key = "#id")
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

    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, key = "#id")
    public void softDeleteProduct(Long id) {
        logger.info("Soft deleting product with id: {}", id);
        Product product = getProductById(id);
        productRepository.softDeleteById(id);
    }
} 