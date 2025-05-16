package com.catalog.catalog_service.mapper;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.model.category;
import com.catalog.catalog_service.model.product;
import com.catalog.catalog_service.model.inventory;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public CategoryDTO toCategoryDTO(category category) {
        if (category == null) return null;
        
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageId(category.getImageId());
        dto.setImageUrl(category.getImageUrl());
        dto.setStatus(category.getStatus());
        return dto;
    }

    public category toCategory(CategoryDTO dto) {
        if (dto == null) return null;
        
        category category = new category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageId(dto.getImageId());
        category.setImageUrl(dto.getImageUrl());
        category.setStatus(dto.getStatus());
        return category;
    }

    public ProductDTO toProductDTO(product product) {
        if (product == null) return null;
        
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setImageIds(product.getImageIds());
        dto.setImageUrls(product.getImageUrls());
        return dto;
    }

    public product toProduct(ProductDTO dto) {
        if (dto == null) return null;
        
        product product = new product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        // Note: Category needs to be set separately as it requires a category entity
        product.setImageIds(dto.getImageIds());
        product.setImageUrls(dto.getImageUrls());
        return product;
    }

    public InventoryDTO toInventoryDTO(inventory inventory) {
        if (inventory == null) return null;
        
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProduct() != null ? inventory.getProduct().getId() : null);
        dto.setCurrentStock(inventory.getCurrentStock());
        dto.setAvailableStock(inventory.getAvailableStock());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setReorderLevel(inventory.getReorderLevel());
        dto.setReorderQuantity(inventory.getReorderQuantity());
        dto.setLowStockThreshold(inventory.getLowStockThreshold());
        dto.setUnitCost(inventory.getUnitCost());
        dto.setLocation(inventory.getLocation());
        dto.setStatus(inventory.getStatus());
        dto.setLastStockMovement(inventory.getLastStockMovement());
        dto.setLastMovementType(inventory.getLastMovementType());
        dto.setLastMovementQuantity(inventory.getLastMovementQuantity());
        dto.setSupplierId(inventory.getSupplierId());
        return dto;
    }

    public inventory toInventory(InventoryDTO dto) {
        if (dto == null) return null;
        
        inventory inventory = new inventory();
        inventory.setId(dto.getId());
        // Note: Product needs to be set separately as it requires a product entity
        inventory.setCurrentStock(dto.getCurrentStock());
        inventory.setAvailableStock(dto.getAvailableStock());
        inventory.setReservedQuantity(dto.getReservedQuantity());
        inventory.setReorderLevel(dto.getReorderLevel());
        inventory.setReorderQuantity(dto.getReorderQuantity());
        inventory.setLowStockThreshold(dto.getLowStockThreshold());
        inventory.setUnitCost(dto.getUnitCost());
        inventory.setLocation(dto.getLocation());
        inventory.setStatus(dto.getStatus());
        inventory.setLastStockMovement(dto.getLastStockMovement());
        inventory.setLastMovementType(dto.getLastMovementType());
        inventory.setLastMovementQuantity(dto.getLastMovementQuantity());
        inventory.setSupplierId(dto.getSupplierId());
        return inventory;
    }
} 