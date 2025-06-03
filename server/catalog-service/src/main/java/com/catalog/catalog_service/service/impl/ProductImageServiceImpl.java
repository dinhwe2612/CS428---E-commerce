package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.ProductImageDTO;
import com.catalog.catalog_service.dto.request.CreateProductImageRequest;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.model.ProductImage;
import com.catalog.catalog_service.repository.jpa.ProductImageRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public ProductImageDTO createProductImage(CreateProductImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId()));

        ProductImage newProductImage = new ProductImage();
        newProductImage.setProduct(product);
        newProductImage.setImageUrl(request.getImageUrl());
        newProductImage.setImageOrder(request.getImageOrder());

        ProductImage savedProductImage = productImageRepository.save(newProductImage);
        return entityMapper.toProductImageDTO(savedProductImage);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found with id: " + id));
        productImageRepository.delete(productImage);
    }
}
