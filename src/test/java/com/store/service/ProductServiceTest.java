package com.store.service;

import com.store.exception.ProductNotFoundException;
import com.store.model.Product;
import com.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);
    private static final String TEST_SEPARATOR = "=".repeat(80);

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data...");
        // Create a test product with initial values
        testProduct = new Product("Test Product", new BigDecimal("99.99"));
        
        // Use reflection to set the ID since it's managed by JPA
        try {
            java.lang.reflect.Field idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testProduct, 1L);
            logger.info("Test product created with ID: {}", testProduct.getId());
        } catch (Exception e) {
            logger.error("Failed to set test product ID", e);
            throw new RuntimeException("Failed to set test product ID", e);
        }
    }

    @Test
    void addProduct_ShouldThrowException_WhenProductIsNull() {
        logger.info("\n{}\n>>> TEST: addProduct_ShouldThrowException_WhenProductIsNull <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        assertThatThrownBy(() -> productService.addProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product cannot be null");
        
        verify(productRepository, never()).save(any(Product.class));
    }

    // Business Logic Tests
    @Test
    void addProduct_ShouldReturnSavedProduct() {
        logger.info("\n{}\n>>> TEST: addProduct_ShouldReturnSavedProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product savedProduct = productService.addProduct(testProduct);
        
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("99.99"));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        logger.info("\n{}\n>>> TEST: getProductById_ShouldReturnProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Product foundProduct = productService.getProductById(1L);
        
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(1L);
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        logger.info("\n{}\n>>> TEST: getProductById_ShouldThrowException_WhenProductNotFound <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
        
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void updatePrice_ShouldReturnUpdatedProduct() {
        logger.info("\n{}\n>>> TEST: updatePrice_ShouldReturnUpdatedProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        BigDecimal newPrice = new BigDecimal("149.99");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product updatedProduct = productService.updatePrice(1L, newPrice);
        
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updatePrice_ShouldThrowException_WhenProductNotFound() {
        logger.info("\n{}\n>>> TEST: updatePrice_ShouldThrowException_WhenProductNotFound <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> productService.updatePrice(999L, new BigDecimal("149.99")))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
        
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        logger.info("\n{}\n>>> TEST: getAllProducts_ShouldReturnListOfProducts <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        List<Product> productList = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(productList);
        
        List<Product> foundProducts = productService.getAllProducts();
        
        assertThat(foundProducts).isNotNull();
        assertThat(foundProducts).hasSize(1);
        assertThat(foundProducts.get(0).getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findAll();
    }
} 