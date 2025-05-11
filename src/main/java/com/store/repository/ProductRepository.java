package com.store.repository;

import com.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p")
    List<Product> findAllIncludingDeleted();
} 