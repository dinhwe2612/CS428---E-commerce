package com.catalog.catalog_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private List<T> content;
    
    @JsonProperty("page_number")
    private int pageNumber;
    
    @JsonProperty("page_size")
    private int pageSize;
    
    @JsonProperty("total_elements")
    private long totalElements;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    private boolean last;
    private boolean first;
} 