package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.category;
import com.catalog.catalog_service.repository.CategoryRepository;
import com.catalog.catalog_service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EntityMapper entityMapper) {
        this.categoryRepository = categoryRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(entityMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return entityMapper.toCategoryDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        category category = new category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageId(request.getImageId());
        category.setImageUrl(request.getImageUrl());
        category.setStatus(request.getStatus());
        
        category savedCategory = categoryRepository.save(category);
        return entityMapper.toCategoryDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, UpdateCategoryRequest request) {
        category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getImageId() != null) {
            category.setImageId(request.getImageId());
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        category updatedCategory = categoryRepository.save(category);
        return entityMapper.toCategoryDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
} 