package com.catalog.catalog_service.repository.jpa;

import java.math.BigDecimal;
import java.util.List;

import com.catalog.catalog_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    List<Product> findAll();
    List<Product> findByIdIn(List<Long> ids);
    
    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) ASC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllWithPriceFiltersAndSorting(@Param("name") String name,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("minPrice") BigDecimal minPrice,
                                                   @Param("maxPrice") BigDecimal maxPrice,
                                                   Pageable pageable);
    
    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllWithPriceFiltersAndSortingDesc(@Param("name") String name,
                                                       @Param("categoryId") Long categoryId,
                                                       @Param("minPrice") BigDecimal minPrice,
                                                       @Param("maxPrice") BigDecimal maxPrice,
                                                       Pageable pageable);

    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) ASC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllSortByPrice(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     Pageable pageable);

    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllSortByPriceDesc(@Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice,
                                         Pageable pageable);

    // New methods for price filtering with name sorting
    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY LOWER(p.name) ASC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllWithPriceFiltersOrderByNameAsc(@Param("name") String name,
                                                        @Param("categoryId") Long categoryId,
                                                        @Param("minPrice") BigDecimal minPrice,
                                                        @Param("maxPrice") BigDecimal maxPrice,
                                                        Pageable pageable);

    @Query(value = """
        SELECT p.* FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        ORDER BY LOWER(p.name) DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM products p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
          AND (:minPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) >= :minPrice)
          AND (:maxPrice IS NULL OR CAST(REPLACE(p.price, ',', '') AS DECIMAL(10,2)) <= :maxPrice)
        """,
        nativeQuery = true)
    Page<Product> findAllWithPriceFiltersOrderByNameDesc(@Param("name") String name,
                                                         @Param("categoryId") Long categoryId,
                                                         @Param("minPrice") BigDecimal minPrice,
                                                         @Param("maxPrice") BigDecimal maxPrice,
                                                         Pageable pageable);
}