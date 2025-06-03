package com.catalog.catalog_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Category;
import com.catalog.catalog_service.repository.jpa.CategoryRepository;
import com.catalog.catalog_service.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;
    private CategoryDTO sampleCategoryDTO;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Prepare a Category entity with the new fields
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");
        sampleCategory.setCategoryPath("/test-path");
        sampleCategory.setTitle("Test Title");
        sampleCategory.setDescription("Test Description");
        sampleCategory.setImageUrl("http://example.com/image.jpg");
        sampleCategory.setProducts(Collections.emptyList());

        // Prepare a corresponding CategoryDTO
        sampleCategoryDTO = new CategoryDTO();
        sampleCategoryDTO.setId(1L);
        sampleCategoryDTO.setName("Test Category");
        sampleCategoryDTO.setCategoryPath("/test-path");
        sampleCategoryDTO.setTitle("Test Title");
        sampleCategoryDTO.setDescription("Test Description");
        sampleCategoryDTO.setImageUrl("http://example.com/image.jpg");

        // Prepare a CreateCategoryRequest matching the new fields
        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setCategoryPath("/new-path");
        createRequest.setTitle("New Title");
        createRequest.setDescription("New Description");
        createRequest.setImageUrl("http://example.com/new-image.jpg");

        // Prepare an UpdateCategoryRequest with updated values
        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setCategoryPath("/updated-path");
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setImageUrl("http://example.com/updated-image.jpg");
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
        CategoryDTO returned = result.get(0);
        assertEquals(sampleCategoryDTO.getId(), returned.getId());
        assertEquals(sampleCategoryDTO.getName(), returned.getName());
        assertEquals(sampleCategoryDTO.getCategoryPath(), returned.getCategoryPath());
        assertEquals(sampleCategoryDTO.getTitle(), returned.getTitle());
        assertEquals(sampleCategoryDTO.getDescription(), returned.getDescription());
        assertEquals(sampleCategoryDTO.getImageUrl(), returned.getImageUrl());
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
        assertEquals(sampleCategoryDTO.getCategoryPath(), result.getCategoryPath());
        assertEquals(sampleCategoryDTO.getTitle(), result.getTitle());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
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
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleCategoryDTO.getId(), result.getId());
        assertEquals(sampleCategoryDTO.getName(), result.getName());
        assertEquals(sampleCategoryDTO.getCategoryPath(), result.getCategoryPath());
        assertEquals(sampleCategoryDTO.getTitle(), result.getTitle());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);
        when(entityMapper.toCategoryDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        // Act
        CategoryDTO result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleCategoryDTO.getId(), result.getId());
        assertEquals(sampleCategoryDTO.getName(), result.getName());
        assertEquals(sampleCategoryDTO.getCategoryPath(), result.getCategoryPath());
        assertEquals(sampleCategoryDTO.getTitle(), result.getTitle());
        assertEquals(sampleCategoryDTO.getDescription(), result.getDescription());
        assertEquals(sampleCategoryDTO.getImageUrl(), result.getImageUrl());
        verify(categoryRepository).save(any(Category.class));
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
