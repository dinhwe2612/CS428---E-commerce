package com.catalog.catalog_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Generic paginated response wrapper")
public class PageDTO<T> {
    
    @Schema(description = "List of items in the current page")
    private List<T> content;
    
    @JsonProperty("page_number")
    @Schema(description = "Current page number (zero-based)", example = "0")
    private int pageNumber;
    
    @JsonProperty("page_size")
    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;
    
    @JsonProperty("total_elements")
    @Schema(description = "Total number of elements across all pages", example = "100")
    private long totalElements;
    
    @JsonProperty("total_pages")
    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;
    
    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;
    
    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;
} 