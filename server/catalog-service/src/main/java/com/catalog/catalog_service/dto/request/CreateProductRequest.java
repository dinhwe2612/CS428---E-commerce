package com.catalog.catalog_service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Product path is required")
    private String productPath;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private String price;

    @Size(max = 100000, message = "Description HTML must be less than 100000 characters")
    private String descriptionHtml;

    @Size(max = 1000, message = "Description text must be less than 1000 characters")
    private String descriptionText;

    private List<@NotBlank(message = "Image URL cannot be blank") String> imageUrls;
}