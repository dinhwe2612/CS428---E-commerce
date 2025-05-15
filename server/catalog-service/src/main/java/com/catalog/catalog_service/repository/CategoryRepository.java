package com.catalog.catalog_service.repository;

import com.catalog.catalog_service.model.category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<category, Long> {
} 