package com.catalog.catalog_service.service;

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

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.category;
import com.catalog.catalog_service.repository.CategoryRepository;
import com.catalog.catalog_service.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private category sampleCategory;
    private CategoryDTO sampleCategoryDTO;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");
        sampleCategory.setDescription("Test Description");
        sampleCategory.setImageId("img123");
        sampleCategory.setImageUrl("http://example.com/image.jpg");
        sampleCategory.setStatus("ACTIVE");

        sampleCategoryDTO = new CategoryDTO();
        sampleCategoryDTO.setId(1L);
        sampleCategoryDTO.setName("Test Category");
        sampleCategoryDTO.setDescription("Test Description");
        sampleCategoryDTO.setImageId("img123");
        sampleCategoryDTO.setImageUrl("http://example.com/image.jpg");
        sampleCategoryDTO.setStatus("ACTIVE");

        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setDescription("New Description");
        createRequest.setImageId("img456");
        createRequest.setImageUrl("http://example.com/new-image.jpg");
        createRequest.setStatus("ACTIVE");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setDescription("Updated Description");
        updateRequest.setImageId("img789");
        updateRequest.setImageUrl("http://example.com/updated-image.jpg");
        updateRequest.setStatus("INACTIVE");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(sampleCategory));
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CategoryDTO returnedCategory = result.get(0);
        assertEquals(sampleCategoryDTO.getId(), returnedCategory.getId());
        assertEquals(sampleCategoryDTO.getName(), returnedCategory.getName());
        assertEquals(sampleCategoryDTO.getDescription(), returnedCategory.getDescription());
        assertEquals(sampleCategoryDTO.getImageId(), returnedCategory.getImageId());
        assertEquals(sampleCategoryDTO.getImageUrl(), returnedCategory.getImageUrl());
        assertEquals(sampleCategoryDTO.getStatus(), returnedCategory.getStatus());
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(sampleCategoryDTO.getId(), result.getId());
        assertEquals(sampleCategoryDTO.getName(), result.getName());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageId(), result.getImageId());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
        assertEquals(sampleCategoryDTO.getStatus(), result.getStatus());
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() {
        // Arrange
        when(categoryRepository.save(any(category.class))).thenReturn(sampleCategory);
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleCategoryDTO.getId(), result.getId());
        assertEquals(sampleCategoryDTO.getName(), result.getName());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageId(), result.getImageId());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
        assertEquals(sampleCategoryDTO.getStatus(), result.getStatus());
        verify(categoryRepository).save(any(category.class));
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(category.class))).thenReturn(sampleCategory);
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        CategoryDTO result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleCategoryDTO.getId(), result.getId());
        assertEquals(sampleCategoryDTO.getName(), result.getName());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageId(), result.getImageId());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
        assertEquals(sampleCategoryDTO.getStatus(), result.getStatus());
        verify(categoryRepository).save(any(category.class));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, updateRequest));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }
} 