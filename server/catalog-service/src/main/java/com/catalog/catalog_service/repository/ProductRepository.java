package com.catalog.catalog_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.catalog.catalog_service.model.product;

@Repository
public interface ProductRepository extends JpaRepository<product, Long>, JpaSpecificationExecutor<product> {
    Page<product> findAll(Pageable pageable);

    Page<product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<product> findByNameContaining(String name, Pageable pageable);

    List<product> findAll();
}