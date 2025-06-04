 package com.catalog.catalog_service.dto.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchRequest {
    
    private String name;
    
    private Double minPrice;
    
    private Double maxPrice;
    
    private Long categoryId;
    
    private int page = 0;
    
    private int size = 10;
} 