package com.catalog.catalog_service.service;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import java.util.List;

public interface InventoryService {
    List<InventoryDTO> getAllInventories();
    InventoryDTO getInventoryById(Long id);
    InventoryDTO createInventory(CreateInventoryRequest request);
    InventoryDTO updateInventory(Long id, UpdateInventoryRequest request);
    void deleteInventory(Long id);
    List<InventoryDTO> getInventoriesByProductId(Long productId);
    void decreaseInventoryQuantity(Long id, Integer quantity);
}