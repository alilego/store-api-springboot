package com.store.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PRODUCTS_CACHE = "products";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(PRODUCTS_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)                 // Maximum number of entries in cache
                .expireAfterWrite(1, TimeUnit.HOURS)  // Entries expire after 1 hour
                .recordStats());                  // Enable statistics
        return cacheManager;
    }
} 