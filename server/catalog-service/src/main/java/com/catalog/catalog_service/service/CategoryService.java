package com.catalog.catalog_service.service;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CreateCategoryRequest request);
    CategoryDTO updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);
} 