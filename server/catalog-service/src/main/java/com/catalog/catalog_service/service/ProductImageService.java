package com.catalog.catalog_service.service;

import com.catalog.catalog_service.dto.ProductImageDTO;
import com.catalog.catalog_service.dto.request.CreateProductImageRequest;

public interface ProductImageService {
    ProductImageDTO createProductImage(CreateProductImageRequest request);
    void deleteProductImage(Long id);
}
