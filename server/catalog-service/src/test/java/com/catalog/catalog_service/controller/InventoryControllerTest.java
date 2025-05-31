package com.catalog.catalog_service.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private InventoryDTO sampleInventoryDTO;
    private CreateInventoryRequest createRequest;
    private UpdateInventoryRequest updateRequest;

    @BeforeEach
    void setUp() {
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
    void getAllInventories_ShouldReturnListOfInventories() throws Exception {
        List<InventoryDTO> inventories = Arrays.asList(sampleInventoryDTO);
        when(inventoryService.getAllInventories()).thenReturn(inventories);

        mockMvc.perform(get("/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()));
    }

    @Test
    void getInventoryById_ShouldReturnInventory() throws Exception {
        when(inventoryService.getInventoryById(anyLong())).thenReturn(sampleInventoryDTO);

        mockMvc.perform(get("/inventories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.currentStock").value(sampleInventoryDTO.getCurrentStock()));
    }

    @Test
    void getInventoryByProductId_ShouldReturnInventory() throws Exception {
        when(inventoryService.getInventoryByProductId(anyLong())).thenReturn(sampleInventoryDTO);

        mockMvc.perform(get("/inventories/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.currentStock").value(sampleInventoryDTO.getCurrentStock()));
    }

    @Test
    void createInventory_ShouldReturnCreatedInventory() throws Exception {
        when(inventoryService.createInventory(any(CreateInventoryRequest.class))).thenReturn(sampleInventoryDTO);

        mockMvc.perform(post("/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.currentStock").value(sampleInventoryDTO.getCurrentStock()));
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventory() throws Exception {
        when(inventoryService.updateInventory(anyLong(), any(UpdateInventoryRequest.class))).thenReturn(sampleInventoryDTO);

        mockMvc.perform(put("/inventories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.currentStock").value(sampleInventoryDTO.getCurrentStock()));
    }

    @Test
    void deleteInventory_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/inventories/1"))
                .andExpect(status().isNoContent());
    }
} 