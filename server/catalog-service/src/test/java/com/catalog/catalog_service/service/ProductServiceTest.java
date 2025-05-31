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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.category;
import com.catalog.catalog_service.model.product;
import com.catalog.catalog_service.repository.CategoryRepository;
import com.catalog.catalog_service.repository.ProductRepository;
import com.catalog.catalog_service.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private product sampleProduct;
    private ProductDTO sampleProductDTO;
    private category sampleCategory;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");

        sampleProduct = new product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setDescription("Test Description");
        sampleProduct.setPrice(99.99);
        sampleProduct.setCategory(sampleCategory);

        sampleProductDTO = new ProductDTO();
        sampleProductDTO.setId(1L);
        sampleProductDTO.setName("Test Product");
        sampleProductDTO.setDescription("Test Description");
        sampleProductDTO.setPrice(99.99);
        sampleProductDTO.setCategoryId(1L);

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
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<product> products = Arrays.asList(sampleProduct);
        Page<product> productPage = new PageImpl<>(products);
        
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(productPage);
        when(entityMapper.toProductDTO(any(product.class)))
            .thenReturn(sampleProductDTO);

        // Act
        PageDTO<ProductDTO> result = productService.getAllProducts(pageable, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(sampleProductDTO, result.getContent().get(0));
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(productRepository.save(any(product.class))).thenReturn(sampleProduct);
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        // Act
        ProductDTO result = productService.createProduct(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
        verify(productRepository).save(any(product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(createRequest));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(product.class))).thenReturn(sampleProduct);
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        // Act
        ProductDTO result = productService.updateProduct(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
        verify(productRepository).save(any(product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateRequest));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void getProductsByCategoryId_ShouldReturnListOfProducts() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findAll()).thenReturn(Arrays.asList(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        // Act
        List<ProductDTO> result = productService.getProductsByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleProductDTO, result.get(0));
    }

    @Test
    void getProductsByCategoryId_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductsByCategoryId(1L));
    }

    @Test
    void getAll_ShouldReturnListOfProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        // Act
        List<ProductDTO> result = productService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleProductDTO, result.get(0));
    }
} 