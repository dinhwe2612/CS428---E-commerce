package com.catalog.catalog_service.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.inventory;
import com.catalog.catalog_service.model.product;
import com.catalog.catalog_service.repository.InventoryRepository;
import com.catalog.catalog_service.repository.ProductRepository;
import com.catalog.catalog_service.service.impl.InventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private inventory sampleInventory;
    private InventoryDTO sampleInventoryDTO;
    private product sampleProduct;
    private CreateInventoryRequest createRequest;
    private UpdateInventoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");

        sampleInventory = new inventory();
        sampleInventory.setId(1L);
        sampleInventory.setProduct(sampleProduct);
        sampleInventory.setCurrentStock(100);
        sampleInventory.setAvailableStock(80);
        sampleInventory.setReservedQuantity(20);
        sampleInventory.setReorderLevel(20);
        sampleInventory.setReorderQuantity(50);
        sampleInventory.setLowStockThreshold(10);
        sampleInventory.setUnitCost(new BigDecimal("10.99"));
        sampleInventory.setLocation("Warehouse A");
        sampleInventory.setStatus("ACTIVE");
        sampleInventory.setSupplierId(1L);

        sampleInventoryDTO = new InventoryDTO();
        sampleInventoryDTO.setId(1L);
        sampleInventoryDTO.setProductId(1L);
        sampleInventoryDTO.setCurrentStock(100);
        sampleInventoryDTO.setAvailableStock(80);
        sampleInventoryDTO.setReservedQuantity(20);
        sampleInventoryDTO.setReorderLevel(20);
        sampleInventoryDTO.setReorderQuantity(50);
        sampleInventoryDTO.setLowStockThreshold(10);
        sampleInventoryDTO.setUnitCost(new BigDecimal("10.99"));
        sampleInventoryDTO.setLocation("Warehouse A");
        sampleInventoryDTO.setStatus("ACTIVE");
        sampleInventoryDTO.setSupplierId(1L);

        createRequest = new CreateInventoryRequest();
        createRequest.setProductId(1L);
        createRequest.setCurrentStock(100);
        createRequest.setAvailableStock(80);
        createRequest.setReservedQuantity(20);
        createRequest.setReorderLevel(20);
        createRequest.setReorderQuantity(50);
        createRequest.setLowStockThreshold(10);
        createRequest.setUnitCost(new BigDecimal("10.99"));
        createRequest.setLocation("Warehouse A");
        createRequest.setStatus("ACTIVE");
        createRequest.setSupplierId(1L);

        updateRequest = new UpdateInventoryRequest();
        updateRequest.setCurrentStock(90);
        updateRequest.setAvailableStock(70);
        updateRequest.setReservedQuantity(20);
        updateRequest.setReorderLevel(20);
        updateRequest.setReorderQuantity(50);
        updateRequest.setLowStockThreshold(10);
        updateRequest.setUnitCost(new BigDecimal("11.99"));
        updateRequest.setLocation("Warehouse B");
        updateRequest.setStatus("ACTIVE");
        updateRequest.setSupplierId(1L);
    }

    @Test
    void getAllInventories_ShouldReturnListOfInventories() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(sampleInventory));
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        List<InventoryDTO> result = inventoryService.getAllInventories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        InventoryDTO returnedInventory = result.get(0);
        assertEquals(sampleInventoryDTO.getId(), returnedInventory.getId());
        assertEquals(sampleInventoryDTO.getProductId(), returnedInventory.getProductId());
        assertEquals(sampleInventoryDTO.getCurrentStock(), returnedInventory.getCurrentStock());
        assertEquals(sampleInventoryDTO.getAvailableStock(), returnedInventory.getAvailableStock());
        assertEquals(sampleInventoryDTO.getReservedQuantity(), returnedInventory.getReservedQuantity());
        assertEquals(sampleInventoryDTO.getReorderLevel(), returnedInventory.getReorderLevel());
        assertEquals(sampleInventoryDTO.getReorderQuantity(), returnedInventory.getReorderQuantity());
        assertEquals(sampleInventoryDTO.getLowStockThreshold(), returnedInventory.getLowStockThreshold());
        assertEquals(sampleInventoryDTO.getUnitCost(), returnedInventory.getUnitCost());
        assertEquals(sampleInventoryDTO.getLocation(), returnedInventory.getLocation());
        assertEquals(sampleInventoryDTO.getStatus(), returnedInventory.getStatus());
        assertEquals(sampleInventoryDTO.getSupplierId(), returnedInventory.getSupplierId());
    }

    @Test
    void getInventoryById_ShouldReturnInventory() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleInventory));
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        InventoryDTO result = inventoryService.getInventoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(sampleInventoryDTO.getId(), result.getId());
        assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
        assertEquals(sampleInventoryDTO.getCurrentStock(), result.getCurrentStock());
        assertEquals(sampleInventoryDTO.getAvailableStock(), result.getAvailableStock());
        assertEquals(sampleInventoryDTO.getReservedQuantity(), result.getReservedQuantity());
        assertEquals(sampleInventoryDTO.getReorderLevel(), result.getReorderLevel());
        assertEquals(sampleInventoryDTO.getReorderQuantity(), result.getReorderQuantity());
        assertEquals(sampleInventoryDTO.getLowStockThreshold(), result.getLowStockThreshold());
        assertEquals(sampleInventoryDTO.getUnitCost(), result.getUnitCost());
        assertEquals(sampleInventoryDTO.getLocation(), result.getLocation());
        assertEquals(sampleInventoryDTO.getStatus(), result.getStatus());
        assertEquals(sampleInventoryDTO.getSupplierId(), result.getSupplierId());
    }

    @Test
    void getInventoryById_ShouldThrowException_WhenInventoryNotFound() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryById(1L));
    }

    @Test
    void createInventory_ShouldReturnCreatedInventory() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.save(any(inventory.class))).thenReturn(sampleInventory);
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        InventoryDTO result = inventoryService.createInventory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleInventoryDTO.getId(), result.getId());
        assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
        assertEquals(sampleInventoryDTO.getCurrentStock(), result.getCurrentStock());
        assertEquals(sampleInventoryDTO.getAvailableStock(), result.getAvailableStock());
        assertEquals(sampleInventoryDTO.getReservedQuantity(), result.getReservedQuantity());
        assertEquals(sampleInventoryDTO.getReorderLevel(), result.getReorderLevel());
        assertEquals(sampleInventoryDTO.getReorderQuantity(), result.getReorderQuantity());
        assertEquals(sampleInventoryDTO.getLowStockThreshold(), result.getLowStockThreshold());
        assertEquals(sampleInventoryDTO.getUnitCost(), result.getUnitCost());
        assertEquals(sampleInventoryDTO.getLocation(), result.getLocation());
        assertEquals(sampleInventoryDTO.getStatus(), result.getStatus());
        assertEquals(sampleInventoryDTO.getSupplierId(), result.getSupplierId());
        verify(inventoryRepository).save(any(inventory.class));
    }

    @Test
    void createInventory_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.createInventory(createRequest));
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventory() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleInventory));
        when(inventoryRepository.save(any(inventory.class))).thenReturn(sampleInventory);
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        InventoryDTO result = inventoryService.updateInventory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleInventoryDTO.getId(), result.getId());
        assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
        assertEquals(sampleInventoryDTO.getCurrentStock(), result.getCurrentStock());
        assertEquals(sampleInventoryDTO.getAvailableStock(), result.getAvailableStock());
        assertEquals(sampleInventoryDTO.getReservedQuantity(), result.getReservedQuantity());
        assertEquals(sampleInventoryDTO.getReorderLevel(), result.getReorderLevel());
        assertEquals(sampleInventoryDTO.getReorderQuantity(), result.getReorderQuantity());
        assertEquals(sampleInventoryDTO.getLowStockThreshold(), result.getLowStockThreshold());
        assertEquals(sampleInventoryDTO.getUnitCost(), result.getUnitCost());
        assertEquals(sampleInventoryDTO.getLocation(), result.getLocation());
        assertEquals(sampleInventoryDTO.getStatus(), result.getStatus());
        assertEquals(sampleInventoryDTO.getSupplierId(), result.getSupplierId());
        verify(inventoryRepository).save(any(inventory.class));
    }

    @Test
    void updateInventory_ShouldThrowException_WhenInventoryNotFound() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.updateInventory(1L, updateRequest));
    }

    @Test
    void deleteInventory_ShouldDeleteInventory() {
        // Arrange
        when(inventoryRepository.existsById(1L)).thenReturn(true);

        // Act
        inventoryService.deleteInventory(1L);

        // Assert
        verify(inventoryRepository).deleteById(1L);
    }

    @Test
    void deleteInventory_ShouldThrowException_WhenInventoryNotFound() {
        // Arrange
        when(inventoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.deleteInventory(1L));
    }

    @Test
    void getInventoryByProductId_ShouldReturnInventory() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(sampleInventory));
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        List<InventoryDTO> results = inventoryService.getInventoriesByProductId(1L);

        // Assert
        results.forEach(result -> {
            assertNotNull(result);
            assertEquals(sampleInventoryDTO.getId(), result.getId());
            assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
            assertEquals(sampleInventoryDTO.getCurrentStock(), result.getCurrentStock());
            assertEquals(sampleInventoryDTO.getAvailableStock(), result.getAvailableStock());
            assertEquals(sampleInventoryDTO.getReservedQuantity(), result.getReservedQuantity());
            assertEquals(sampleInventoryDTO.getReorderLevel(), result.getReorderLevel());
            assertEquals(sampleInventoryDTO.getReorderQuantity(), result.getReorderQuantity());
            assertEquals(sampleInventoryDTO.getLowStockThreshold(), result.getLowStockThreshold());
            assertEquals(sampleInventoryDTO.getUnitCost(), result.getUnitCost());
            assertEquals(sampleInventoryDTO.getLocation(), result.getLocation());
            assertEquals(sampleInventoryDTO.getStatus(), result.getStatus());
            assertEquals(sampleInventoryDTO.getSupplierId(), result.getSupplierId());
        });
    }

    @Test
    void getInventoryByProductId_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoriesByProductId(1L));
    }

    @Test
    void getInventoryByProductId_ShouldThrowException_WhenInventoryNotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoriesByProductId(1L));
    }
} 