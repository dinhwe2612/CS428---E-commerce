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
import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import com.catalog.catalog_service.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO sampleCategory;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new CategoryDTO();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");
        sampleCategory.setCategoryPath("/test-path");
        sampleCategory.setTitle("Test Header");
        sampleCategory.setDescription("Test Description");
        sampleCategory.setImageUrl("http://example.com/image.jpg");

        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setCategoryPath("/new-path");
        createRequest.setTitle("New Header");
        createRequest.setDescription("New Description");
        createRequest.setImageUrl("http://example.com/new-image.jpg");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setCategoryPath("/updated-path");
        updateRequest.setTitle("Updated Header");
        updateRequest.setDescription("Updated Description");
        updateRequest.setImageUrl("http://example.com/updated-image.jpg");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        List<CategoryDTO> categories = Arrays.asList(sampleCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleCategory.getId()))
                .andExpect(jsonPath("$[0].name").value(sampleCategory.getName()))
                .andExpect(jsonPath("$[0].categoryPath").value(sampleCategory.getCategoryPath()))
                .andExpect(jsonPath("$[0].title").value(sampleCategory.getTitle()))
                .andExpect(jsonPath("$[0].description").value(sampleCategory.getDescription()))
                .andExpect(jsonPath("$[0].imageUrl").value(sampleCategory.getImageUrl()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCategoryById_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(sampleCategory);

        mockMvc.perform(get("/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCategory.getId()))
                .andExpect(jsonPath("$.name").value(sampleCategory.getName()))
                .andExpect(jsonPath("$.categoryPath").value(sampleCategory.getCategoryPath()))
                .andExpect(jsonPath("$.title").value(sampleCategory.getTitle()))
                .andExpect(jsonPath("$.description").value(sampleCategory.getDescription()))
                .andExpect(jsonPath("$.imageUrl").value(sampleCategory.getImageUrl()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                .thenReturn(sampleCategory);

        mockMvc.perform(post("/categories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCategory.getId()))
                .andExpect(jsonPath("$.name").value(sampleCategory.getName()))
                .andExpect(jsonPath("$.categoryPath").value(sampleCategory.getCategoryPath()))
                .andExpect(jsonPath("$.title").value(sampleCategory.getTitle()))
                .andExpect(jsonPath("$.description").value(sampleCategory.getDescription()))
                .andExpect(jsonPath("$.imageUrl").value(sampleCategory.getImageUrl()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(any(Long.class), any(UpdateCategoryRequest.class)))
                .thenReturn(sampleCategory);

        mockMvc.perform(put("/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCategory.getId()))
                .andExpect(jsonPath("$.name").value(sampleCategory.getName()))
                .andExpect(jsonPath("$.categoryPath").value(sampleCategory.getCategoryPath()))
                .andExpect(jsonPath("$.title").value(sampleCategory.getTitle()))
                .andExpect(jsonPath("$.description").value(sampleCategory.getDescription()))
                .andExpect(jsonPath("$.imageUrl").value(sampleCategory.getImageUrl()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }
}
