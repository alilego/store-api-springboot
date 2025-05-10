package com.store.service;

import com.store.exception.ProductNotFoundException;
import com.store.exception.ProductVersionMismatchException;
import com.store.model.Product;
import com.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void addProduct_ShouldPersistAndReturnProduct() {
        // Arrange
        Product newProduct = new Product("Integration Test Product", new BigDecimal("199.99"));

        // Act
        Product savedProduct = productService.addProduct(newProduct);

        // Assert
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Integration Test Product");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    @Test
    void getProductById_ShouldReturnPersistedProduct() {
        // Arrange
        Product newProduct = new Product("Test Product", new BigDecimal("299.99"));
        Product savedProduct = productService.addProduct(newProduct);

        // Act
        Product retrievedProduct = productService.getProductById(savedProduct.getId());

        // Assert
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(retrievedProduct.getName()).isEqualTo("Test Product");
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
    }

    @Test
    void updatePrice_ShouldUpdateProductPrice() {
        // Arrange
        Product newProduct = new Product("Price Update Test", new BigDecimal("399.99"));
        Product savedProduct = productService.addProduct(newProduct);
        BigDecimal newPrice = new BigDecimal("449.99");

        // Act
        Product updatedProduct = productService.updatePrice(savedProduct.getId(), newPrice, null);

        // Assert
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(newPrice);
        
        // Verify the price is actually updated in the database
        Product retrievedProduct = productService.getProductById(savedProduct.getId());
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(newPrice);
    }

    @Test
    void updatePrice_ShouldThrowException_WhenVersionMismatch() {
        // Arrange
        Product newProduct = new Product("Version Test", new BigDecimal("499.99"));
        Product savedProduct = productService.addProduct(newProduct);
        
        // First update to increment version
        Product savedProduct1 = productService.updatePrice(savedProduct.getId(), new BigDecimal("489.99"), null);
        Integer firstVersion = savedProduct1.getVersion();
        productRepository.flush();  // Force transaction commit
        
        // Second update to increment version again
        Product savedProduct2 = productService.updatePrice(savedProduct.getId(), new BigDecimal("479.99"), null);
        Integer secondVersion = savedProduct2.getVersion();
        productRepository.flush();  // Force transaction commit
        
        // Verify version was incremented
        assertThat(secondVersion).isGreaterThan(firstVersion);
        
        // Now try to update with the old version
        BigDecimal newPrice = new BigDecimal("549.99");
        assertThatThrownBy(() -> productService.updatePrice(savedProduct.getId(), newPrice, firstVersion))
                .isInstanceOf(ProductVersionMismatchException.class)
                .hasMessageContaining("Your version of product with id=" + savedProduct.getId() + " is outdated");
    }

    @Test
    void getAllProducts_ShouldReturnAllPersistedProducts() {
        // Arrange
        Product product1 = new Product("Product 1", new BigDecimal("100.00"));
        Product product2 = new Product("Product 2", new BigDecimal("200.00"));
        productService.addProduct(product1);
        productService.addProduct(product2);

        // Act
        List<Product> allProducts = productService.getAllProducts();

        // Assert
        assertThat(allProducts).hasSize(2);
        assertThat(allProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
    }
} 