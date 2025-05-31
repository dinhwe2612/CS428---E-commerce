package com.catalog.catalog_service.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;
import com.catalog.catalog_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO sampleProduct;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductDTO();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setDescription("Test Description");
        sampleProduct.setPrice(99.99);
        sampleProduct.setCategoryId(1L);

        createRequest = new CreateProductRequest();
        createRequest.setName("New Product");
        createRequest.setDescription("New Description");
        createRequest.setPrice(149.99);
        createRequest.setCategoryId(1L);

        updateRequest = new UpdateProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(199.99);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        List<ProductDTO> products = Arrays.asList(sampleProduct);
        PageDTO<ProductDTO> pageDTO = new PageDTO<>(products, 0, 10, 1, 1, true, true);

        when(productService.getAllProducts(any(), any(), any(), any(), any()))
            .thenReturn(pageDTO);

        mockMvc.perform(get("/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(get("/products/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class)))
            .thenReturn(sampleProduct);

        mockMvc.perform(post("/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(any(Long.class), any(UpdateProductRequest.class)))
            .thenReturn(sampleProduct);

        mockMvc.perform(put("/products/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/products/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductsByCategory_ShouldReturnPageOfProducts() throws Exception {
        List<ProductDTO> products = Arrays.asList(sampleProduct);
        PageDTO<ProductDTO> pageDTO = new PageDTO<>(products, 0, 10, 1, 1, true, true);

        when(productService.getAllProducts(any(), any(), any(), any(), any()))
            .thenReturn(pageDTO);

        mockMvc.perform(get("/products/category/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        List<ProductDTO> products = Arrays.asList(sampleProduct);
        when(productService.getAll()).thenReturn(products);

        mockMvc.perform(get("/products/all")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
} 