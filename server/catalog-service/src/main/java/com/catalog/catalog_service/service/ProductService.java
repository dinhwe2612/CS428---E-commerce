package com.catalog.catalog_service.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;

public interface ProductService {
    PageDTO<ProductDTO> getAllProducts(Pageable pageable, String name, Double minPrice, Double maxPrice,
            Long categoryId);

    ProductDTO getProductById(Long id);

    ProductDTO createProduct(CreateProductRequest request);

    ProductDTO updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    List<ProductDTO> getProductsByCategoryId(Long categoryId);

    List<ProductDTO> getAll();

    List<ProductDTO> getProductsByIds(List<Long> ids);
}