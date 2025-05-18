package com.microservice_ecommerce.integration.catalog.constants;

public final class CatalogEndpoints {
    public static final String API_PATH = "/api/v1/internal";
    
    public static final String PRODUCTS_PATH = API_PATH + "/products";
    
    public static String getProductDetails(Long productId) {
        return PRODUCTS_PATH + "/" + productId;
    }
    
    
    private CatalogEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
} 