package com.catalog.catalog_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.catalog_service.dto.AutocompleteResponse;
import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.category;
import com.catalog.catalog_service.model.product;
import com.catalog.catalog_service.repository.CategoryRepository;
import com.catalog.catalog_service.repository.ProductRepository;
import com.catalog.catalog_service.repository.ProductSearchRepository;
import com.catalog.catalog_service.service.ProductService;
import com.catalog.catalog_service.specification.ProductSpecification;
import com.catalog.catalog_service.model.ProductDocument;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;
    private final ProductSearchRepository productSearchRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, 
                            CategoryRepository categoryRepository,
                            EntityMapper entityMapper,
                            ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
        this.productSearchRepository = productSearchRepository;
    }

    @Override
    public PageDTO<ProductDTO> getAllProducts(Pageable pageable, String name, Double minPrice, Double maxPrice, Long categoryId) {
        Specification<product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);
        Page<product> productPage = productRepository.findAll(spec, pageable);
        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(entityMapper::toProductDTO)
                .collect(Collectors.toList());
        
        return new PageDTO<>(
            productDTOs,
            productPage.getNumber(),
            productPage.getSize(),
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.isLast(),
            productPage.isFirst()
        );
    }

    @Override
    public ProductDTO getProductById(Long id) {
        product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return entityMapper.toProductDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        try {
            logger.debug("Creating product with request: {}", request);
            
            category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Category not found with id: {}", request.getCategoryId());
                        return new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
                    });

            logger.debug("Found category: {}", category);

            product product = new product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setCategory(category);
            product.setImageIds(request.getImageIds());
            product.setImageUrls(request.getImageUrls());

            logger.debug("Saving product: {}", product);
            product savedProduct = productRepository.save(product);
            
            // Index in Elasticsearch
            ProductDocument document = ProductDocument.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
            productSearchRepository.save(document);
            
            logger.debug("Product saved successfully with id: {}", savedProduct.getId());
            
            return entityMapper.toProductDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getImageIds() != null) {
            product.setImageIds(request.getImageIds());
        }
        if (request.getImageUrls() != null) {
            product.setImageUrls(request.getImageUrls());
        }

        product updatedProduct = productRepository.save(product);
        return entityMapper.toProductDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .map(entityMapper::toProductDTO)
                .collect(Collectors.toList());
    }
    @Override
     public List<ProductDTO> getAll(){
        List<product> products = productRepository.findAll();
        return products.stream()
                .map(entityMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        List<product> products = productRepository.findByIdIn(ids);
        return products.stream()
                .map(entityMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AutocompleteResponse getAutocompleteSuggestions(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return new AutocompleteResponse(List.of());
        }

        String searchQuery = query.trim().toLowerCase();
        List<String> suggestions = productSearchRepository
            .findByNameContainingOrDescriptionContaining(searchQuery, searchQuery)
            .stream()
            .map(ProductDocument::getName)
            .distinct()
            .limit(limit)
            .collect(Collectors.toList());

        return new AutocompleteResponse(suggestions);
    }
}