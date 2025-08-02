package com.catalog.catalog_service.model;

public enum ProductStatus {
    NEW_FLOWER("New Flower"),
    OLD_FLOWER("Old Flower"),
    LOW_STOCK("Low Stock");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 