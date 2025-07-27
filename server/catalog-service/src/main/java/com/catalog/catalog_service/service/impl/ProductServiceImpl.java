package com.catalog.catalog_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.catalog_service.dto.AutocompleteResponse;
import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;
import com.catalog.catalog_service.event.ProductCreatedEvent;
import com.catalog.catalog_service.event.ProductDeletedEvent;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Category;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.model.ProductDocument;
import com.catalog.catalog_service.model.ProductImage;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.repository.es.ProductSearchRepository;
import com.catalog.catalog_service.repository.jpa.CategoryRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.ProductService;
import com.catalog.catalog_service.specification.ProductSpecification;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;
    private final ProductSearchRepository productSearchRepository;
    private final RabbitProducer rabbitProducer;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, 
                            CategoryRepository categoryRepository,
                            EntityMapper entityMapper,
                            ProductSearchRepository productSearchRepository,
                            RabbitProducer rabbitProducer) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
        this.productSearchRepository = productSearchRepository;
        this.rabbitProducer = rabbitProducer;
    }

    @Override
    public PageDTO<ProductDTO> getAllProducts(Pageable pageable, String name, Double minPrice, Double maxPrice, Long categoryId) {
        // Check if sorting by price is needed
        boolean hasPriceSorting = pageable.getSort().stream()
                .anyMatch(order -> "price".equalsIgnoreCase(order.getProperty()));
        
        Page<Product> productPage;
        if (hasPriceSorting) {
            // For price sorting, we need to fetch all products, sort them, then paginate
            // Get all products without pagination
            Specification<Product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);
            List<Product> allProducts = productRepository.findAll(spec);
            
            // Apply price sorting to all products
            List<Product> allSortedProducts = allProducts.stream()
                    .sorted((p1, p2) -> {
                        try {
                            // Remove commas and convert to double
                            double price1 = Double.parseDouble(p1.getPrice().replace(",", ""));
                            double price2 = Double.parseDouble(p2.getPrice().replace(",", ""));
                            
                            Sort.Order priceOrder = pageable.getSort().stream()
                                    .filter(order -> "price".equalsIgnoreCase(order.getProperty()))
                                    .findFirst()
                                    .orElse(Sort.Order.asc("price"));
                            
                            int comparison = Double.compare(price1, price2);
                            return priceOrder.getDirection() == Sort.Direction.DESC ? -comparison : comparison;
                        } catch (NumberFormatException e) {
                            // If price parsing fails, keep original order
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());
            
            // Apply pagination to the sorted list
            int pageSize = pageable.getPageSize();
            int pageNumber = pageable.getPageNumber();
            int startIndex = pageNumber * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allSortedProducts.size());
            
            List<Product> pageContent = startIndex < allSortedProducts.size() 
                ? allSortedProducts.subList(startIndex, endIndex) 
                : new ArrayList<>();
            
            // Create a new page with the paginated sorted content
            productPage = new PageImpl<>(pageContent, pageable, allSortedProducts.size());
        } else {
            // No price sorting, use normal specification
            Specification<Product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryId);
            productPage = productRepository.findAll(spec, pageable);
        }
        
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return entityMapper.toProductDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        try {
            // check if category is existed
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Category not found with id: {}", request.getCategoryId());
                        return new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
                    });

            // set product fields
            Product product = new Product();
            product.setName(request.getName());
            product.setProductPath(request.getProductPath());
            product.setDescriptionHtml(request.getDescriptionHtml());
            product.setDescriptionText(request.getDescriptionText());
            product.setPrice(request.getPrice());
            product.setCategory(category);
            List<String> imageUrls = request.getImageUrls();
            if (imageUrls != null) {
                AtomicInteger counter = new AtomicInteger(1);
                imageUrls.forEach(imageUrl -> {
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setImageOrder(counter.getAndIncrement());
                    product.addImage(productImage);
                });
            }

            // save product
            Product savedProduct = productRepository.save(product);
            
            // Index in Elasticsearch
           // ProductDocument document = ProductDocument.builder()
           //     .id(savedProduct.getId())
            //    .name(savedProduct.getName())
             //   .description(savedProduct.getDescriptionText())
             //   .price(Double.valueOf(savedProduct.getPrice()))
              //  .categoryId(category.getId())
              //  .categoryName(category.getName())
              //  .build();
            //productSearchRepository.save(document);
            rabbitProducer.sendProductCreatedEvent(new ProductCreatedEvent(savedProduct.getId(), savedProduct.getName(), savedProduct.getDescriptionText(), Double.valueOf(savedProduct.getPrice()), category.getId(), category.getName(), savedProduct.getProductPath()));
            
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getProductPath() != null) {
            product.setProductPath(request.getProductPath());
        }
        if (request.getDescriptionHtml() != null) {
            product.setDescriptionHtml(request.getDescriptionHtml());
        }
        if (request.getDescriptionText() != null) {
            product.setDescriptionText(request.getDescriptionText());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        List<String> imageUrls = request.getImageUrls();
        if (imageUrls != null) {
            AtomicInteger counter = new AtomicInteger(1);
            List<ProductImage> productImages = imageUrls.stream()
                    .map(imageUrl -> {
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imageUrl);
                        productImage.setImageOrder(counter.getAndIncrement());
                        return productImage;
                    })
                    .toList();
            product.setImages(productImages);
        }

        Product updatedProduct = productRepository.save(product);
        return entityMapper.toProductDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        rabbitProducer.sendProductDeletedEvent(new ProductDeletedEvent(id
        )
        );
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
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(entityMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        List<Product> products = productRepository.findByIdIn(ids);
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
        logger.debug("Searching for suggestions with query: {}", searchQuery);
        
        List<String> suggestions = productSearchRepository
            .searchSuggestions(searchQuery)
            .stream()
            .map(ProductDocument::getName)
            .distinct()
            .limit(limit)
            .collect(Collectors.toList());

        logger.debug("Found {} suggestions for query: {}", suggestions.size(), searchQuery);
        return new AutocompleteResponse(suggestions);
    }
}