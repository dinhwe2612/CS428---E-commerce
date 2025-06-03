package com.catalog.catalog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Long categoryId;
    private String productPath;
    private String name;
    private String price;
    private String descriptionHtml;
    private String descriptionText;
    private List<String> imageUrls;
}