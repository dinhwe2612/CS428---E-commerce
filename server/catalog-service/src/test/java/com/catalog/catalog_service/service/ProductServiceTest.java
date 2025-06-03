package com.catalog.catalog_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import com.catalog.catalog_service.model.Category;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.repository.jpa.CategoryRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
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

    private Product sampleProduct;
    private ProductDTO sampleProductDTO;
    private Category sampleCategory;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");
        sampleCategory.setCategoryPath("/test");
        sampleCategory.setTitle("Test Title");
        sampleCategory.setDescription("Test description");
        sampleCategory.setImageUrl("http://example.com/cat.jpg");
        sampleCategory.setProducts(Collections.emptyList());

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setCategory(sampleCategory);
        sampleProduct.setProductPath("/test-product");
        sampleProduct.setName("Test Product");
        sampleProduct.setPrice("99.99");
        sampleProduct.setDescriptionHtml("<p>Test Description</p>");
        sampleProduct.setDescriptionText("Test Description");
        sampleProduct.setImages(Collections.emptyList());

        sampleProductDTO = new ProductDTO();
        sampleProductDTO.setId(1L);
        sampleProductDTO.setCategoryId(1L);
        sampleProductDTO.setProductPath("/test-product");
        sampleProductDTO.setName("Test Product");
        sampleProductDTO.setPrice("99.99");
        sampleProductDTO.setDescriptionHtml("<p>Test Description</p>");
        sampleProductDTO.setDescriptionText("Test Description");
        sampleProductDTO.setImageUrls(Collections.emptyList());

        createRequest = new CreateProductRequest();
        createRequest.setCategoryId(1L);
        createRequest.setProductPath("/new-product");
        createRequest.setName("New Product");
        createRequest.setPrice("149.99");
        createRequest.setDescriptionHtml("<p>New Description</p>");
        createRequest.setDescriptionText("New Description");
        createRequest.setImageUrls(Collections.emptyList());

        updateRequest = new UpdateProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setPrice("199.99");
        updateRequest.setDescriptionHtml("<p>Updated Description</p>");
        updateRequest.setDescriptionText("Updated Description");
        updateRequest.setImageUrls(Collections.emptyList());
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(sampleProduct);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(productPage);
        when(entityMapper.toProductDTO(any(Product.class)))
                .thenReturn(sampleProductDTO);

        PageDTO<ProductDTO> result = productService.getAllProducts(pageable, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(sampleProductDTO, result.getContent().get(0));
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        ProductDTO result = productService.createProduct(createRequest);

        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(createRequest));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        ProductDTO result = productService.updateProduct(1L, updateRequest);

        assertNotNull(result);
        assertEquals(sampleProductDTO, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateRequest));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void getProductsByCategoryId_ShouldReturnListOfProducts() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findAll()).thenReturn(Arrays.asList(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        List<ProductDTO> result = productService.getProductsByCategoryId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleProductDTO, result.get(0));
    }

    @Test
    void getProductsByCategoryId_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductsByCategoryId(1L));
    }

    @Test
    void getAll_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(sampleProduct));
        when(entityMapper.toProductDTO(sampleProduct)).thenReturn(sampleProductDTO);

        List<ProductDTO> result = productService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleProductDTO, result.get(0));
    }
}
