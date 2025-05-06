package com.store.config;

import com.store.model.Product;
import com.store.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            logger.info("Initializing database with banking products...");
            
            List<Product> products = List.of(
                new Product("Personal Checking Account", new BigDecimal("0.00")),
                new Product("Business Checking Account", new BigDecimal("15.00")),
                new Product("Savings Account", new BigDecimal("0.00")),
                new Product("High-Yield Savings Account", new BigDecimal("0.00")),
                new Product("Certificate of Deposit (1 Year)", new BigDecimal("1000.00")),
                new Product("Certificate of Deposit (5 Year)", new BigDecimal("1000.00")),
                new Product("Personal Loan", new BigDecimal("0.00")),
                new Product("Business Loan", new BigDecimal("0.00")),
                new Product("Mortgage Loan", new BigDecimal("0.00")),
                new Product("Credit Card (Basic)", new BigDecimal("0.00")),
                new Product("Credit Card (Premium)", new BigDecimal("95.00")),
                new Product("Investment Account", new BigDecimal("0.00"))
            );

            repository.saveAll(products);
            logger.info("Database initialized with {} banking products", products.size());
        };
    }
} 