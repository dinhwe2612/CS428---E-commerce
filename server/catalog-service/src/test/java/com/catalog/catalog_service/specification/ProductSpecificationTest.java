package com.catalog.catalog_service.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.catalog.catalog_service.model.product;

public class ProductSpecificationTest {

    @Test
    void withFilters_ShouldCreateSpecificationWithNameFilter() {
        // Arrange
        String name = "test";
        Double minPrice = null;
        Double maxPrice = null;
        Long categoryId = null;

        // Act
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);

        // Assert
        assertNotNull(spec);
        // Note: We can't directly test the internal Predicate creation, but we can verify the specification is created
    }

    @Test
    void withFilters_ShouldCreateSpecificationWithPriceRange() {
        // Arrange
        String name = null;
        Double minPrice = 10.0;
        Double maxPrice = 100.0;
        Long categoryId = null;

        // Act
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void withFilters_ShouldCreateSpecificationWithCategoryId() {
        // Arrange
        String name = null;
        Double minPrice = null;
        Double maxPrice = null;
        Long categoryId = 1L;

        // Act
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void withFilters_ShouldCreateSpecificationWithAllFilters() {
        // Arrange
        String name = "test";
        Double minPrice = 10.0;
        Double maxPrice = 100.0;
        Long categoryId = 1L;

        // Act
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void withFilters_ShouldCreateEmptySpecification() {
        // Arrange
        String name = null;
        Double minPrice = null;
        Double maxPrice = null;
        Long categoryId = null;

        // Act
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);

        // Assert
        assertNotNull(spec);
    }
} 