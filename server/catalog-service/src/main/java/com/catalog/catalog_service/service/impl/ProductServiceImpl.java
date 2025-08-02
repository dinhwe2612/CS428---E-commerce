package com.catalog.catalog_service.service.impl;

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
import com.catalog.catalog_service.model.ProductStatus;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.repository.es.ProductSearchRepository;
import com.catalog.catalog_service.repository.jpa.CategoryRepository;
import com.catalog.catalog_service.repository.jpa.ProductImageRepository;
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
    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, 
                            CategoryRepository categoryRepository,
                            EntityMapper entityMapper,
                            ProductSearchRepository productSearchRepository,
                            RabbitProducer rabbitProducer,
                            ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
        this.productSearchRepository = productSearchRepository;
        this.rabbitProducer = rabbitProducer;
        this.productImageRepository = productImageRepository;
    }
    @Override
    public PageDTO<ProductDTO> getAllProducts(
            Pageable pageable,
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId) {
    
        boolean hasPriceSorting = pageable.getSort().stream()
            .anyMatch(o -> "price".equalsIgnoreCase(o.getProperty()));
    
        // only name & category in the DB
        Specification<Product> spec =
            ProductSpecification.withFilters(name, null, null, categoryId);
    
        Page<Product> productPage;
    
        if (hasPriceSorting) {
            // 1) load everything matching name/category
            List<Product> allProducts = productRepository.findAll(spec);
    
            // 2) APPLY numeric filter FIRST
            List<Product> filtered = allProducts.stream()
                .filter(p -> {
                    try {
                        double v = Double.parseDouble(p.getPrice().replace(",", ""));
                        return (minPrice == null || v >= minPrice)
                            && (maxPrice == null || v <= maxPrice);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .toList();
    
            // 3) SORT *that* filtered list (not the original allProducts)
            Sort.Order priceOrder = pageable.getSort().stream()
                .filter(o -> "price".equalsIgnoreCase(o.getProperty()))
                .findFirst()
                .orElse(Sort.Order.asc("price"));
    
            List<Product> sorted = filtered.stream()
                .sorted((p1, p2) -> {
                    double a = Double.parseDouble(p1.getPrice().replace(",", ""));
                    double b = Double.parseDouble(p2.getPrice().replace(",", ""));
                    int cmp = Double.compare(a, b);
                    return priceOrder.isAscending() ? cmp : -cmp;
                })
                .toList();
    
            // 4) PAGINATE in Java
            int pageSize  = pageable.getPageSize();
            int pageNum   = pageable.getPageNumber();
            int start     = pageNum * pageSize;
            int end       = Math.min(start + pageSize, sorted.size());
            List<Product> pageContent = start < sorted.size()
                ? sorted.subList(start, end)
                : List.of();
    
            productPage = new PageImpl<>(pageContent, pageable, sorted.size());
        }
        else {
            // 1) Fetch *all* matching products (name & category only)
            List<Product> all = productRepository.findAll(spec);
        
            // 2) Apply min/max price filter in Java
            List<Product> filtered = all.stream()
                .filter(p -> {
                    try {
                        double v = Double.parseDouble(p.getPrice().replace(",", ""));
                        return (minPrice == null || v >= minPrice)
                            && (maxPrice == null || v <= maxPrice);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .toList();
        
            // 3) Sort by name according to sortDirection
            Sort.Order nameOrder = pageable.getSort().stream()
                .filter(o -> "name".equalsIgnoreCase(o.getProperty()))
                .findFirst()
                .orElse(Sort.Order.asc("name"));
        
            List<Product> sorted = filtered.stream()
                .sorted((p1, p2) -> {
                    int cmp = p1.getName().compareToIgnoreCase(p2.getName());
                    return nameOrder.isAscending() ? cmp : -cmp;
                })
                .toList();
        
            // 4) Paginate the sorted list in Java
            int size  = pageable.getPageSize();
            int pageN = pageable.getPageNumber();
            int start = pageN * size;
            int end   = Math.min(start + size, sorted.size());
            List<Product> pageContent = start < sorted.size()
                ? sorted.subList(start, end)
                : List.of();
        
            // 5) Wrap into a PageImpl—with the correct total = sorted.size()
            productPage = new PageImpl<>(pageContent, pageable, sorted.size());
        }
        // map & return
        List<ProductDTO> dtos = productPage.getContent().stream()
            .map(entityMapper::toProductDTO)
            .toList();
    
        return new PageDTO<>(
            dtos,
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
            product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.NEW_FLOWER);
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
        logger.debug("Starting updateProduct for id: {}", id);
        logger.debug("UpdateProductRequest: {}", request);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getProductPath() != null) {
            product.setProductPath(request.getProductPath());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
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
            productImageRepository.deleteByProductId(product.getId());
            productImageRepository.flush();
            AtomicInteger counter = new AtomicInteger(1);
        
            // 1) Clear out the old ones so orphanRemoval=true will delete them
            product.getImages().clear();
        
            // 2) Build & wire each new image
            List<ProductImage> productImages = imageUrls.stream()
                .map(imageUrl -> {
                    ProductImage pi = new ProductImage();
                    pi.setImageUrl(imageUrl);
                    pi.setImageOrder(counter.getAndIncrement());
                    // ← set the FK so product_id isn’t null
                    pi.setProduct(product);
                    return pi;
                })
                .toList();
        
            // 3) Add them into the *managed* collection
            product.getImages().addAll(productImages);
        }
        
        logger.debug("Product after update: {}", product);

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

    @Override
    @Transactional
    public void syncAllProductsToNewFlower() {
        logger.info("Starting sync of all products to NEW_FLOWER status");
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            product.setStatus(ProductStatus.NEW_FLOWER);
        }
        
        productRepository.saveAll(allProducts);
        logger.info("Successfully synced {} products to NEW_FLOWER status", allProducts.size());
    }
}