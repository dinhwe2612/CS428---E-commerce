package com.catalog.catalog_service.repository;

import com.catalog.catalog_service.model.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<inventory, Long> {
} 