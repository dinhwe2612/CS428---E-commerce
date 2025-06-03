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
        sampleInventoryDTO.setQuantity(100);
        sampleInventoryDTO.setArrivalDate(null);
        sampleInventoryDTO.setDescription("Initial stock");
        sampleInventoryDTO.setVersion(1L);

        // Wrap it into a List<InventoryDTO>
        sampleInventoriesDTO = Arrays.asList(sampleInventoryDTO);

        // Prepare CreateInventoryRequest
        createRequest = new CreateInventoryRequest();
        createRequest.setProductId(1L);
        createRequest.setQuantity(100);
        createRequest.setArrivalDate(null);
        createRequest.setDescription("Initial stock");

        // Prepare UpdateInventoryRequest
        updateRequest = new UpdateInventoryRequest();
        updateRequest.setQuantity(90);
        updateRequest.setArrivalDate(null);
        updateRequest.setDescription("Adjusted stock");
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
                .andExpect(jsonPath("$[0].quantity").value(sampleInventoryDTO.getQuantity()))
                .andExpect(jsonPath("$[0].description").value(sampleInventoryDTO.getDescription()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInventoryById_ShouldReturnSingleInventory() throws Exception {
        when(inventoryService.getInventoryById(anyLong())).thenReturn(sampleInventoryDTO);

        mockMvc.perform(get("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                // Response is a single object
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.quantity").value(sampleInventoryDTO.getQuantity()))
                .andExpect(jsonPath("$.description").value(sampleInventoryDTO.getDescription()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInventoriesByProductId_ShouldReturnListOfInventory() throws Exception {
        when(inventoryService.getInventoriesByProductId(anyLong())).thenReturn(sampleInventoriesDTO);

        mockMvc.perform(get("/inventories/product/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                // Assert on the first element in the array
                .andExpect(jsonPath("$[0].id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$[0].productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$[0].quantity").value(sampleInventoryDTO.getQuantity()))
                .andExpect(jsonPath("$[0].description").value(sampleInventoryDTO.getDescription()))
                .andExpect(jsonPath("$[0].version").value(sampleInventoryDTO.getVersion()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInventory_ShouldReturnCreatedInventory() throws Exception {
        when(inventoryService.createInventory(any(CreateInventoryRequest.class)))
                .thenReturn(sampleInventoryDTO);

        mockMvc.perform(post("/inventories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                // Single-object response: verify fields directly
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.quantity").value(sampleInventoryDTO.getQuantity()))
                .andExpect(jsonPath("$.description").value(sampleInventoryDTO.getDescription()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInventory_ShouldReturnUpdatedInventory() throws Exception {
        when(inventoryService.updateInventory(anyLong(), any(UpdateInventoryRequest.class)))
                .thenReturn(sampleInventoryDTO);

        mockMvc.perform(put("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                // Single-object response: verify fields directly
                .andExpect(jsonPath("$.id").value(sampleInventoryDTO.getId()))
                .andExpect(jsonPath("$.productId").value(sampleInventoryDTO.getProductId()))
                .andExpect(jsonPath("$.quantity").value(sampleInventoryDTO.getQuantity()))
                .andExpect(jsonPath("$.description").value(sampleInventoryDTO.getDescription()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInventory_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/inventories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }
}
