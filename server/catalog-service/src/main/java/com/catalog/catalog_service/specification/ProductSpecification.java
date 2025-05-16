package com.catalog.catalog_service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.catalog.catalog_service.model.product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<product> withFilters(
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = name.trim().toLowerCase();
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + searchTerm + "%"
                ));
            }
            
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 