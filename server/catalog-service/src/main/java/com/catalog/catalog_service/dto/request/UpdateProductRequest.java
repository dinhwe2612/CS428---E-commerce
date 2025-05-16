package com.catalog.catalog_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private Long categoryId;
    private List<Long> imageIds;
    private List<String> imageUrls;
} 