package com.catalog.catalog_service.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
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
    private PageDTO<ProductDTO> pageDTO;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductDTO();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setDescription("Test Description");
        sampleProduct.setPrice(99.99);
        sampleProduct.setCategoryId(1L);
        sampleProduct.setImageIds(Arrays.asList(1L, 2L));
        sampleProduct.setImageUrls(Arrays.asList("http://example.com/image1.jpg", "http://example.com/image2.jpg"));

        createRequest = new CreateProductRequest();
        createRequest.setName("New Product");
        createRequest.setDescription("New Description");
        createRequest.setPrice(149.99);
        createRequest.setCategoryId(1L);
        createRequest.setImageIds(Arrays.asList(3L, 4L));
        createRequest.setImageUrls(Arrays.asList("http://example.com/image3.jpg", "http://example.com/image4.jpg"));

        updateRequest = new UpdateProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(199.99);
        updateRequest.setCategoryId(2L);
        updateRequest.setImageIds(Arrays.asList(5L, 6L));
        updateRequest.setImageUrls(Arrays.asList("http://example.com/image5.jpg", "http://example.com/image6.jpg"));

        pageDTO = new PageDTO<>(
            Arrays.asList(sampleProduct),
            0,
            10,
            1,
            1,
            true,
            true
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        when(productService.getAllProducts(any(Pageable.class), any(), any(), any(), any()))
            .thenReturn(pageDTO);

        mockMvc.perform(get("/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.content[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.content[0].description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.content[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.content[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.content[0].imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$.content[0].imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$.content[0].imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.content[0].imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(get("/products/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$.imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$.imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
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
                .andExpect(jsonPath("$.id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$.imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$.imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
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
                .andExpect(jsonPath("$.id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$.imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$.imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
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
    void getProductsByCategory_ShouldReturnListOfProducts() throws Exception {
        when(productService.getAllProducts(any(Pageable.class), any(), any(), any(), any()))
            .thenReturn(pageDTO);

        mockMvc.perform(get("/products/category/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.content[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.content[0].description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.content[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.content[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.content[0].imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$.content[0].imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$.content[0].imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.content[0].imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfProducts() throws Exception {
        when(productService.getAll()).thenReturn(Arrays.asList(sampleProduct));

        mockMvc.perform(get("/products/all")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$[0].description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$[0].imageIds[0]").value(sampleProduct.getImageIds().get(0)))
                .andExpect(jsonPath("$[0].imageIds[1]").value(sampleProduct.getImageIds().get(1)))
                .andExpect(jsonPath("$[0].imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$[0].imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
    }
} 