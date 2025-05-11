package com.store.service;

import com.store.exception.ProductNotFoundException;
import com.store.exception.ProductVersionMismatchException;
import com.store.model.Product;
import com.store.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear existing data
        productRepository.deleteAll();
        entityManager.flush();

        // Create test products
        List<Product> products = List.of(
            new Product("Product 1", new BigDecimal("10.00")),
            new Product("Product 2", new BigDecimal("20.00")),
            new Product("Product 3", new BigDecimal("30.00")),
            new Product("Product 4", new BigDecimal("40.00")),
            new Product("Product 5", new BigDecimal("50.00"))
        );
        products.forEach(product -> {
            productRepository.save(product);
            entityManager.flush();
        });
        entityManager.clear();
    }

    // Create Product Tests
    @Test
    void addProduct_ShouldPersistAndReturnProduct() {
        // Arrange
        Product newProduct = new Product("Test Product", new BigDecimal("299.99"));

        // Act
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Product retrievedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(retrievedProduct.getId()).isNotNull();
        assertThat(retrievedProduct.getName()).isEqualTo("Test Product");
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
        assertThat(retrievedProduct.getVersion()).isZero();
        assertThat(retrievedProduct.getCreatedAt()).isNotNull();
        assertThat(retrievedProduct.getUpdatedAt()).isNotNull();
    }

    // Get Product Tests
    @Test
    void getProductById_ShouldReturnPersistedProduct() {
        // Arrange
        Product newProduct = new Product("Test Product", new BigDecimal("299.99"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

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
        assertThatThrownBy(() -> productService.getProductById(999L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product not found with id: 999");
    }

    // Update Price Tests
    @Test
    void updatePrice_ShouldUpdateProductPrice_WithVersion() {
        // Arrange
        Product newProduct = new Product("Test Product", new BigDecimal("299.99"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

        // Act
        Product updatedProduct = productService.updatePrice(savedProduct.getId(), new BigDecimal("25.00"), savedProduct.getVersion());
        entityManager.flush();
        entityManager.clear();

        // Assert
        Product retrievedProduct = productRepository.findById(updatedProduct.getId()).orElseThrow();
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(retrievedProduct.getVersion()).isEqualTo(savedProduct.getVersion() + 1);
    }

    @Test
    void updatePrice_ShouldUpdateProductPrice_WithoutVersion() {
        // Arrange
        Product newProduct = new Product("Test Product", new BigDecimal("299.99"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

        // Act
        Product updatedProduct = productService.updatePrice(savedProduct.getId(), new BigDecimal("25.00"), null);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Product retrievedProduct = productRepository.findById(updatedProduct.getId()).orElseThrow();
        assertThat(retrievedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(retrievedProduct.getVersion()).isEqualTo(savedProduct.getVersion() + 1);
    }

    @Test
    void updatePrice_ShouldThrowException_WhenVersionMismatch() {
        // Arrange
        Product newProduct = new Product("Version Test", new BigDecimal("499.99"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();
        
        // First update to increment version
        Product savedProduct1 = productService.updatePrice(savedProduct.getId(), new BigDecimal("489.99"), savedProduct.getVersion());
        entityManager.flush();
        entityManager.clear();
        
        // Second update to increment version again
        Product savedProduct2 = productService.updatePrice(savedProduct.getId(), new BigDecimal("479.99"), savedProduct1.getVersion());
        entityManager.flush();
        entityManager.clear();
        
        // Verify version was incremented
        assertThat(savedProduct2.getVersion()).isGreaterThan(savedProduct1.getVersion());
        
        // Now try to update with the old version
        assertThatThrownBy(() -> 
            productService.updatePrice(savedProduct.getId(), new BigDecimal("549.99"), savedProduct1.getVersion()))
            .isInstanceOf(ProductVersionMismatchException.class)
            .hasMessageContaining("Your version of product with id=" + savedProduct.getId() + " is outdated");
    }

    // Get All Products Tests
    @Test
    void getAllProducts_ShouldReturnPaginatedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 1");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Product 2");
    }

    @Test
    void getAllProducts_ShouldReturnSecondPage() {
        // Given
        Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 3");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Product 4");
    }

    @Test
    void getAllProducts_ShouldReturnLastPage() {
        // Given
        Pageable pageable = PageRequest.of(2, 2, Sort.by("name"));

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 5");
    }

    // Soft Delete Tests
    @Test
    void softDeleteProduct_ShouldMarkProductAsDeleted() {
        // Arrange
        Product newProduct = new Product("Soft Delete Test", new BigDecimal("123.45"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

        // Act
        productService.softDeleteProduct(savedProduct.getId());
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThatThrownBy(() -> productService.getProductById(savedProduct.getId()))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product not found with id: " + savedProduct.getId());
    }

    @Test
    void getAllProducts_ShouldNotReturnSoftDeletedProducts() {
        // Arrange
        Product newProduct = new Product("Soft Delete Test", new BigDecimal("123.45"));
        Product savedProduct = productService.addProduct(newProduct);
        entityManager.flush();
        entityManager.clear();

        productService.softDeleteProduct(savedProduct.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Product> result = productService.getAllProducts(PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent().stream()
            .noneMatch(p -> p.getId().equals(savedProduct.getId()))).isTrue();
    }

    @Test
    void softDeleteProduct_ShouldThrowException_WhenProductNotFound() {
        assertThatThrownBy(() -> productService.softDeleteProduct(999L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product not found with id: 999");
    }
} 