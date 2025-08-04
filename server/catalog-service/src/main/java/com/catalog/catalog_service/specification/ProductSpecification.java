package com.catalog.catalog_service.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.catalog.catalog_service.model.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> withFilters(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long categoryId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchTerm),
                    cb.like(cb.lower(root.get("descriptionText")), searchTerm)
                ));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceValue"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("priceValue"), maxPrice));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
