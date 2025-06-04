package com.catalog.catalog_service.specification;

import java.util.ArrayList;
import java.util.List;

import com.catalog.catalog_service.model.Product;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> withFilters(
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = name.trim().toLowerCase();
                
                List<Predicate> namePredicates = new ArrayList<>();
                namePredicates.add(cb.like(cb.lower(root.get("name")), "%" + searchTerm + "%"));
                namePredicates.add(cb.like(cb.lower(root.get("descriptionText")), "%" + searchTerm + "%"));
                
                String[] words = searchTerm.split("\\s+");
                if (words.length > 1) {
                    for (String word : words) {
                        if (word.length() > 2) {
                            namePredicates.add(cb.like(cb.lower(root.get("name")), "%" + word + "%"));
                            namePredicates.add(cb.like(cb.lower(root.get("descriptionText")), "%" + word + "%"));
                        }
                    }
                }
                
                predicates.add(cb.or(namePredicates.toArray(new Predicate[0])));
            }
            
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    cb.function("CAST", Double.class, root.get("price"), cb.literal(Double.class)),
                    minPrice
                ));
            }
            
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    cb.function("CAST", Double.class, root.get("price"), cb.literal(Double.class)),
                    maxPrice
                ));
            }
            
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 