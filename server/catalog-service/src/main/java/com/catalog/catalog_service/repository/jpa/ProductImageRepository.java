package com.catalog.catalog_service.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.catalog.catalog_service.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteByProductId(Long productId);
}
