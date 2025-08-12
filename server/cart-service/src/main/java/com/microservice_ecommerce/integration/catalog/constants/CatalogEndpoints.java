package com.microservice_ecommerce.integration.catalog.constants;

import java.util.List;
import java.util.stream.Collectors;

public final class CatalogEndpoints {
    public static final String API_PATH = "/api/v1/internal";
    
    public static final String PRODUCTS_PATH = API_PATH + "/products";
    
    public static String getProductDetails(Long productId) {
        return PRODUCTS_PATH + "/" + productId;
    }
    
    public static String getProductsByIds(List<Long> ids) {
        return PRODUCTS_PATH + "/list?ids=" + ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
    
    public static String getAllProducts() {
        return PRODUCTS_PATH + "/all";
    }
    
    private CatalogEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
} 