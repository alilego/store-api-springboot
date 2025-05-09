package com.store.service;

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
    void addProduct_ShouldReturnSavedProduct() {
        // Test: Adding a new product to the store
        logger.info("\n{}\n>>> TEST: addProduct_ShouldReturnSavedProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        // Arrange - Setup the mock behavior
        logger.info("Setting up mock repository to return saved product");
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act - Call the service method
        logger.info("Calling productService.addProduct()");
        Product savedProduct = productService.addProduct(testProduct);

        // Assert - Verify the results
        logger.info("Verifying saved product details");
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("99.99"));
        
        // Verify repository interaction
        logger.info("Verifying repository interaction");
        verify(productRepository, times(1)).save(any(Product.class));
        logger.info("Test completed successfully\n{}", TEST_SEPARATOR);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        // Test: Retrieving a product by its ID
        logger.info("\n{}\n>>> TEST: getProductById_ShouldReturnProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        // Arrange - Setup the mock behavior
        logger.info("Setting up mock repository to return product for ID: {}", 1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act - Call the service method
        logger.info("Calling productService.getProductById()");
        Product foundProduct = productService.getProductById(1L);

        // Assert - Verify the results
        logger.info("Verifying found product details");
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(1L);
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        
        // Verify repository interaction
        logger.info("Verifying repository interaction");
        verify(productRepository, times(1)).findById(1L);
        logger.info("Test completed successfully\n{}", TEST_SEPARATOR);
    }

    @Test
    void updatePrice_ShouldReturnUpdatedProduct() {
        // Test: Updating a product's price
        logger.info("\n{}\n>>> TEST: updatePrice_ShouldReturnUpdatedProduct <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        // Arrange - Setup the mock behavior
        BigDecimal newPrice = new BigDecimal("149.99");
        logger.info("Setting up mock repository for price update to: {}", newPrice);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act - Call the service method
        logger.info("Calling productService.updatePrice()");
        Product updatedProduct = productService.updatePrice(1L, newPrice);

        // Assert - Verify the results
        logger.info("Verifying updated product details");
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);
        
        // Verify repository interactions
        logger.info("Verifying repository interactions");
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        logger.info("Test completed successfully\n{}", TEST_SEPARATOR);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Test: Retrieving all products from the store
        logger.info("\n{}\n>>> TEST: getAllProducts_ShouldReturnListOfProducts <<<\n{}", TEST_SEPARATOR, TEST_SEPARATOR);
        
        // Arrange - Setup the mock behavior
        List<Product> productList = Arrays.asList(testProduct);
        logger.info("Setting up mock repository to return list of products");
        when(productRepository.findAll()).thenReturn(productList);

        // Act - Call the service method
        logger.info("Calling productService.getAllProducts()");
        List<Product> foundProducts = productService.getAllProducts();

        // Assert - Verify the results
        logger.info("Verifying returned product list");
        assertThat(foundProducts).isNotNull();
        assertThat(foundProducts).hasSize(1);
        assertThat(foundProducts.get(0).getId()).isEqualTo(1L);
        
        // Verify repository interaction
        logger.info("Verifying repository interaction");
        verify(productRepository, times(1)).findAll();
        logger.info("Test completed successfully\n{}", TEST_SEPARATOR);
    }
} 