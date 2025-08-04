package com.catalog.catalog_service.repository.jpa;

import java.util.List;
import java.util.Optional;

import com.catalog.catalog_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @EntityGraph(attributePaths = {"images"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
    
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    List<Product> findAll();

    List<Product> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"images"})
    Optional<Product> findById(Long id);
}