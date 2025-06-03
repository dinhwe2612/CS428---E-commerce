package com.catalog.catalog_service.repository.jpa;

import com.catalog.catalog_service.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
