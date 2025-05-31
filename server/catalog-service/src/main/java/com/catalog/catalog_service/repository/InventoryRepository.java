package com.catalog.catalog_service.repository;

import com.catalog.catalog_service.model.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<inventory, Long> {
    @Query(
            value = "SELECT * FROM inventories WHERE product_id = :productId",
            nativeQuery = true
    )
    List<inventory> findinventoriesByProductId(Long productId);
} 