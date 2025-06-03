package com.catalog.catalog_service.controller;

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
        sampleProduct.setCategoryId(1L);
        sampleProduct.setProductPath("/test-product");
        sampleProduct.setName("Test Product");
        sampleProduct.setPrice("99.99");
        sampleProduct.setDescriptionHtml("<p>Test Description</p>");
        sampleProduct.setDescriptionText("Test Description");
        sampleProduct.setImageUrls(Arrays.asList(
                "http://example.com/image1.jpg",
                "http://example.com/image2.jpg"
        ));

        createRequest = new CreateProductRequest();
        createRequest.setCategoryId(1L);
        createRequest.setProductPath("/new-product");
        createRequest.setName("New Product");
        createRequest.setPrice("149.99");
        createRequest.setDescriptionHtml("<p>New Description</p>");
        createRequest.setDescriptionText("New Description");
        createRequest.setImageUrls(Arrays.asList(
                "http://example.com/image3.jpg",
                "http://example.com/image4.jpg"
        ));

        updateRequest = new UpdateProductRequest();
        updateRequest.setCategoryId(2L);
        updateRequest.setName("Updated Product");
        updateRequest.setPrice("199.99");
        updateRequest.setDescriptionHtml("<p>Updated Description</p>");
        updateRequest.setDescriptionText("Updated Description");
        updateRequest.setImageUrls(Arrays.asList(
                "http://example.com/image5.jpg",
                "http://example.com/image6.jpg"
        ));

        pageDTO = new PageDTO<>(
                List.of(sampleProduct),
                0,      // current page
                10,     // size
                1,      // total pages
                1,      // total elements
                true,   // has next
                true    // has previous
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
                .andExpect(jsonPath("$.content[0].productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$.content[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.content[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.content[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.content[0].descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$.content[0].descriptionText").value(sampleProduct.getDescriptionText()))
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
                .andExpect(jsonPath("$.productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$.descriptionText").value(sampleProduct.getDescriptionText()))
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
                .andExpect(jsonPath("$.productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$.descriptionText").value(sampleProduct.getDescriptionText()))
                .andExpect(jsonPath("$.imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$.imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(anyLong(), any(UpdateProductRequest.class)))
                .thenReturn(sampleProduct);

        mockMvc.perform(put("/products/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleProduct.getId()))
                .andExpect(jsonPath("$.productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$.descriptionText").value(sampleProduct.getDescriptionText()))
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
                .andExpect(jsonPath("$.content[0].productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$.content[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.content[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.content[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$.content[0].descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$.content[0].descriptionText").value(sampleProduct.getDescriptionText()))
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
                .andExpect(jsonPath("$[0].productPath").value(sampleProduct.getProductPath()))
                .andExpect(jsonPath("$[0].name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$[0].price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$[0].categoryId").value(sampleProduct.getCategoryId()))
                .andExpect(jsonPath("$[0].descriptionHtml").value(sampleProduct.getDescriptionHtml()))
                .andExpect(jsonPath("$[0].descriptionText").value(sampleProduct.getDescriptionText()))
                .andExpect(jsonPath("$[0].imageUrls[0]").value(sampleProduct.getImageUrls().get(0)))
                .andExpect(jsonPath("$[0].imageUrls[1]").value(sampleProduct.getImageUrls().get(1)));
    }
}
