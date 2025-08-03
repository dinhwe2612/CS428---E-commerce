package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.exception.OutOfStockException;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Inventory;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.repository.jpa.InventoryRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return entityMapper.toInventoryDTO(inventory);
    }

    @Override
    @Transactional
    public InventoryDTO createInventory(CreateInventoryRequest request) {
        Product existingProduct = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Inventory newInventory = new Inventory();
        newInventory.setProduct(existingProduct);
        newInventory.setDescription(request.getDescription());
        newInventory.setQuantity(request.getQuantity());
        newInventory.setArrivalDate(request.getArrivalDate());
        newInventory.setPricingRules(new ArrayList<>());
        
        Inventory savedInventory = inventoryRepository.save(newInventory);
        return entityMapper.toInventoryDTO(savedInventory);
    }

    @Override
    @Transactional
    public InventoryDTO updateInventory(Long id, UpdateInventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        if (request.getDescription() != null) {
            inventory.setDescription(request.getDescription());
        }
        if (request.getQuantity() != null) {
            inventory.setQuantity(request.getQuantity());
        }
        if (request.getArrivalDate() != null) {
            inventory.setArrivalDate(request.getArrivalDate());
        }
        if (request.getProductId() != null) {
            Product existingProduct = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
            inventory.setProduct(existingProduct);
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
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

    @Override
    @Transactional
    public void decreaseInventoryQuantity(Long id, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        if (inventory.getQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock to complete the order. Please try again later.");
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
} 