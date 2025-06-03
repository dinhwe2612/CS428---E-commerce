package com.catalog.catalog_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Inventory;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.repository.jpa.InventoryRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
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

    private Inventory sampleInventory;
    private InventoryDTO sampleInventoryDTO;
    private Product sampleProduct;
    private CreateInventoryRequest createRequest;
    private UpdateInventoryRequest updateRequest;
    private LocalDateTime fixedArrival;

    @BeforeEach
    void setUp() {
        // Fixed arrival date for consistency
        fixedArrival = LocalDateTime.of(2025, 1, 1, 12, 0);

        // Prepare a minimal Product (category can be null)
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setCategory(null);
        sampleProduct.setProductPath("/test-path");
        sampleProduct.setName("Test Product");
        sampleProduct.setPrice("9.99");
        sampleProduct.setDescriptionHtml("<p>Test</p>");
        sampleProduct.setDescriptionText("Test");
        sampleProduct.setImages(Collections.emptyList());

        // Prepare a sample Inventory entity
        sampleInventory = new Inventory();
        sampleInventory.setId(1L);
        sampleInventory.setProduct(sampleProduct);
        sampleInventory.setQuantity(100);
        sampleInventory.setArrivalDate(fixedArrival);
        sampleInventory.setDescription("Initial stock");
        sampleInventory.setVersion(1L);
        sampleInventory.setPricingRules(Collections.emptyList());

        // Prepare the corresponding DTO
        sampleInventoryDTO = new InventoryDTO();
        sampleInventoryDTO.setId(1L);
        sampleInventoryDTO.setProductId(1L);
        sampleInventoryDTO.setQuantity(100);
        sampleInventoryDTO.setArrivalDate(fixedArrival);
        sampleInventoryDTO.setDescription("Initial stock");
        sampleInventoryDTO.setVersion(1L);

        // CreateInventoryRequest uses productId, quantity, arrivalDate, description
        createRequest = new CreateInventoryRequest();
        createRequest.setProductId(1L);
        createRequest.setQuantity(100);
        createRequest.setArrivalDate(fixedArrival);
        createRequest.setDescription("Initial stock");

        // UpdateInventoryRequest allows changing quantity, arrivalDate, description
        updateRequest = new UpdateInventoryRequest();
        updateRequest.setQuantity(80);
        updateRequest.setArrivalDate(fixedArrival.plusDays(1));
        updateRequest.setDescription("Adjusted stock");
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
        InventoryDTO returned = result.get(0);
        assertEquals(sampleInventoryDTO.getId(), returned.getId());
        assertEquals(sampleInventoryDTO.getProductId(), returned.getProductId());
        assertEquals(sampleInventoryDTO.getQuantity(), returned.getQuantity());
        assertEquals(sampleInventoryDTO.getArrivalDate(), returned.getArrivalDate());
        assertEquals(sampleInventoryDTO.getDescription(), returned.getDescription());
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
        assertEquals(sampleInventoryDTO.getQuantity(), result.getQuantity());
        assertEquals(sampleInventoryDTO.getArrivalDate(), result.getArrivalDate());
        assertEquals(sampleInventoryDTO.getDescription(), result.getDescription());
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
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        InventoryDTO result = inventoryService.createInventory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleInventoryDTO.getId(), result.getId());
        assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
        assertEquals(sampleInventoryDTO.getQuantity(), result.getQuantity());
        assertEquals(sampleInventoryDTO.getArrivalDate(), result.getArrivalDate());
        assertEquals(sampleInventoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleInventoryDTO.getVersion(), result.getVersion());
        verify(inventoryRepository).save(any(Inventory.class));
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
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        InventoryDTO result = inventoryService.updateInventory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleInventoryDTO.getId(), result.getId());
        assertEquals(sampleInventoryDTO.getProductId(), result.getProductId());
        assertEquals(sampleInventoryDTO.getQuantity(), result.getQuantity());
        assertEquals(sampleInventoryDTO.getArrivalDate(), result.getArrivalDate());
        assertEquals(sampleInventoryDTO.getDescription(), result.getDescription());
        verify(inventoryRepository).save(any(Inventory.class));
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
    void getInventoryByProductId_ShouldReturnInventoryList() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(sampleInventory));
        when(entityMapper.toInventoryDTO(sampleInventory)).thenReturn(sampleInventoryDTO);

        // Act
        List<InventoryDTO> results = inventoryService.getInventoriesByProductId(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        InventoryDTO returned = results.get(0);
        assertEquals(sampleInventoryDTO.getId(), returned.getId());
        assertEquals(sampleInventoryDTO.getProductId(), returned.getProductId());
        assertEquals(sampleInventoryDTO.getQuantity(), returned.getQuantity());
        assertEquals(sampleInventoryDTO.getArrivalDate(), returned.getArrivalDate());
        assertEquals(sampleInventoryDTO.getDescription(), returned.getDescription());
    }

    @Test
    void getInventoryByProductId_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoriesByProductId(1L));
    }

    @Test
    void getInventoryByProductId_ShouldThrowException_WhenNoInventoriesFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoriesByProductId(1L));
    }
}
