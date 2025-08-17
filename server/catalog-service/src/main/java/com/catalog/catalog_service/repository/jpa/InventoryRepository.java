package com.catalog.catalog_service.repository.jpa;

import com.catalog.catalog_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query(
            value = "SELECT * FROM inventories WHERE product_id = :productId",
            nativeQuery = true
    )
    List<Inventory> findByProductId(Long productId);
    
    boolean existsByProductId(Long productId);
} 