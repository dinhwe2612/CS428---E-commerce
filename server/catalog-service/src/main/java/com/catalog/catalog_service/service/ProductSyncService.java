package com.catalog.catalog_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.model.ProductDocument;
import com.catalog.catalog_service.repository.es.ProductSearchRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductSyncService {
    
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void deleteIndex() {
        try {
            if (elasticsearchOperations.indexOps(ProductDocument.class).exists()) {
                elasticsearchOperations.indexOps(ProductDocument.class).delete();
                log.info("Successfully deleted products index");
            } else {
                log.info("Products index does not exist");
            }
        } catch (Exception e) {
            log.error("Error deleting products index", e);
            throw e;
        }
    }

    @Transactional
    public void syncAllProducts() {
        log.info("Starting product sync to Elasticsearch");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products in MySQL", products.size());
        
        List<ProductDocument> documents = products.stream()
            .map(this::convertToDocument)
            .collect(Collectors.toList());
        
        log.info("Converted {} products to Elasticsearch documents", documents.size());
        productSearchRepository.saveAll(documents);
        log.info("Successfully synced {} products to Elasticsearch", documents.size());
    }

    private ProductDocument convertToDocument(Product product) {
        log.debug("Converting product: id={}, name={}", product.getId(), product.getName());
        return ProductDocument.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescriptionText())
            .price(Double.valueOf(product.getPrice()))
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getName())
            .build();
    }
} 