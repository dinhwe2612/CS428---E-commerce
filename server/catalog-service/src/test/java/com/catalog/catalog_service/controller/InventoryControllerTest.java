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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.catalog.catalog_service.config.SecurityConfig;
import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(InventoryController.class)
@Import(SecurityConfig.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private InventoryDTO sampleInventoryDTO;
    private List<InventoryDTO> sampleInventoriesDTO;
    private CreateInventoryRequest createRequest;
    private UpdateInventoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Build a single InventoryDTO
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

        // Wrap it into a List<InventoryDTO>
        sampleInventoriesDTO = Arrays.asList(sampleInventoryDTO);

        // Prepare CreateInventoryRequest
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

        // Prepare UpdateInventoryRequest
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
    @WithMockUser(roles = "ADMIN")
    void getAllInventories_ShouldReturnListOfInventories() throws Exception {
        when(inventoryService.getAllInventories()).thenReturn(sampleInventoriesDTO);

        mockMvc.perform(get("/inventories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                // Check first element in returned array
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()))
                .andExpect(jsonPath("$[0].reservedQuantity").value(sampleInventoryDTO.getReservedQuantity()))
                .andExpect(jsonPath("$[0].reorderLevel").value(sampleInventoryDTO.getReorderLevel()))
                .andExpect(jsonPath("$[0].reorderQuantity").value(sampleInventoryDTO.getReorderQuantity()))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(sampleInventoryDTO.getLowStockThreshold()))
                .andExpect(jsonPath("$[0].unitCost").value(sampleInventoryDTO.getUnitCost()))
                .andExpect(jsonPath("$[0].location").value(sampleInventoryDTO.getLocation()))
                .andExpect(jsonPath("$[0].status").value(sampleInventoryDTO.getStatus()))
                .andExpect(jsonPath("$[0].supplierId").value(sampleInventoryDTO.getSupplierId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInventoryById_ShouldReturnListOfInventory() throws Exception {
        when(inventoryService.getInventoryById(anyLong())).thenReturn(sampleInventoryDTO);

        mockMvc.perform(get("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                // Response is now an array, so check index [0]
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()))
                .andExpect(jsonPath("$[0].reservedQuantity").value(sampleInventoryDTO.getReservedQuantity()))
                .andExpect(jsonPath("$[0].reorderLevel").value(sampleInventoryDTO.getReorderLevel()))
                .andExpect(jsonPath("$[0].reorderQuantity").value(sampleInventoryDTO.getReorderQuantity()))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(sampleInventoryDTO.getLowStockThreshold()))
                .andExpect(jsonPath("$[0].unitCost").value(sampleInventoryDTO.getUnitCost()))
                .andExpect(jsonPath("$[0].location").value(sampleInventoryDTO.getLocation()))
                .andExpect(jsonPath("$[0].status").value(sampleInventoryDTO.getStatus()))
                .andExpect(jsonPath("$[0].supplierId").value(sampleInventoryDTO.getSupplierId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInventoryByProductId_ShouldReturnListOfInventory() throws Exception {
        when(inventoryService.getInventoriesByProductId(anyLong())).thenReturn(sampleInventoriesDTO);

        mockMvc.perform(get("/inventories/product/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                // Again, assert on the first element in the array
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()))
                .andExpect(jsonPath("$[0].reservedQuantity").value(sampleInventoryDTO.getReservedQuantity()))
                .andExpect(jsonPath("$[0].reorderLevel").value(sampleInventoryDTO.getReorderLevel()))
                .andExpect(jsonPath("$[0].reorderQuantity").value(sampleInventoryDTO.getReorderQuantity()))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(sampleInventoryDTO.getLowStockThreshold()))
                .andExpect(jsonPath("$[0].unitCost").value(sampleInventoryDTO.getUnitCost()))
                .andExpect(jsonPath("$[0].location").value(sampleInventoryDTO.getLocation()))
                .andExpect(jsonPath("$[0].status").value(sampleInventoryDTO.getStatus()))
                .andExpect(jsonPath("$[0].supplierId").value(sampleInventoryDTO.getSupplierId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInventory_ShouldReturnCreatedInventoryInList() throws Exception {
        when(inventoryService.createInventory(any(CreateInventoryRequest.class)))
                .thenReturn(sampleInventoryDTO);

        mockMvc.perform(post("/inventories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                // Now response is an array, so check index [0]
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()))
                .andExpect(jsonPath("$[0].reservedQuantity").value(sampleInventoryDTO.getReservedQuantity()))
                .andExpect(jsonPath("$[0].reorderLevel").value(sampleInventoryDTO.getReorderLevel()))
                .andExpect(jsonPath("$[0].reorderQuantity").value(sampleInventoryDTO.getReorderQuantity()))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(sampleInventoryDTO.getLowStockThreshold()))
                .andExpect(jsonPath("$[0].unitCost").value(sampleInventoryDTO.getUnitCost()))
                .andExpect(jsonPath("$[0].location").value(sampleInventoryDTO.getLocation()))
                .andExpect(jsonPath("$[0].status").value(sampleInventoryDTO.getStatus()))
                .andExpect(jsonPath("$[0].supplierId").value(sampleInventoryDTO.getSupplierId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInventory_ShouldReturnUpdatedInventoryInList() throws Exception {
        when(inventoryService.updateInventory(anyLong(), any(UpdateInventoryRequest.class)))
                .thenReturn(sampleInventoryDTO);

        mockMvc.perform(put("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                // Array response: verify the first element
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].currentStock").value(sampleInventoryDTO.getCurrentStock()))
                .andExpect(jsonPath("$[0].availableStock").value(sampleInventoryDTO.getAvailableStock()))
                .andExpect(jsonPath("$[0].reservedQuantity").value(sampleInventoryDTO.getReservedQuantity()))
                .andExpect(jsonPath("$[0].reorderLevel").value(sampleInventoryDTO.getReorderLevel()))
                .andExpect(jsonPath("$[0].reorderQuantity").value(sampleInventoryDTO.getReorderQuantity()))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(sampleInventoryDTO.getLowStockThreshold()))
                .andExpect(jsonPath("$[0].unitCost").value(sampleInventoryDTO.getUnitCost()))
                .andExpect(jsonPath("$[0].location").value(sampleInventoryDTO.getLocation()))
                .andExpect(jsonPath("$[0].status").value(sampleInventoryDTO.getStatus()))
                .andExpect(jsonPath("$[0].supplierId").value(sampleInventoryDTO.getSupplierId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInventory_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }
}
