package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.inventory;
import com.catalog.catalog_service.model.product;
import com.catalog.catalog_service.repository.InventoryRepository;
import com.catalog.catalog_service.repository.ProductRepository;
import com.catalog.catalog_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                              ProductRepository productRepository,
                              EntityMapper entityMapper) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(entityMapper::toInventoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO getInventoryById(Long id) {
        inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return entityMapper.toInventoryDTO(inventory);
    }

    @Override
    @Transactional
    public InventoryDTO createInventory(CreateInventoryRequest request) {
        product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        inventory inventory = new inventory();
        inventory.setProduct(product);
        inventory.setCurrentStock(request.getCurrentStock());
        inventory.setAvailableStock(request.getAvailableStock());
        inventory.setReservedQuantity(request.getReservedQuantity());
        inventory.setReorderLevel(request.getReorderLevel());
        inventory.setReorderQuantity(request.getReorderQuantity());
        inventory.setLowStockThreshold(request.getLowStockThreshold());
        inventory.setUnitCost(request.getUnitCost());
        inventory.setLocation(request.getLocation());
        inventory.setStatus(request.getStatus());
        inventory.setSupplierId(request.getSupplierId());

        inventory savedInventory = inventoryRepository.save(inventory);
        return entityMapper.toInventoryDTO(savedInventory);
    }

    @Override
    @Transactional
    public InventoryDTO updateInventory(Long id, UpdateInventoryRequest request) {
        inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        if (request.getCurrentStock() != null) {
            inventory.setCurrentStock(request.getCurrentStock());
        }
        if (request.getAvailableStock() != null) {
            inventory.setAvailableStock(request.getAvailableStock());
        }
        if (request.getReservedQuantity() != null) {
            inventory.setReservedQuantity(request.getReservedQuantity());
        }
        if (request.getReorderLevel() != null) {
            inventory.setReorderLevel(request.getReorderLevel());
        }
        if (request.getReorderQuantity() != null) {
            inventory.setReorderQuantity(request.getReorderQuantity());
        }
        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getUnitCost() != null) {
            inventory.setUnitCost(request.getUnitCost());
        }
        if (request.getLocation() != null) {
            inventory.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            inventory.setStatus(request.getStatus());
        }
        if (request.getSupplierId() != null) {
            inventory.setSupplierId(request.getSupplierId());
        }

        inventory updatedInventory = inventoryRepository.save(inventory);
        return entityMapper.toInventoryDTO(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    @Override
    public List<InventoryDTO> getInventoriesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return inventoryRepository.findinventoriesByProductId(productId).stream()
                .map(entityMapper::toInventoryDTO)
                .collect(Collectors.toList());
    }
} 