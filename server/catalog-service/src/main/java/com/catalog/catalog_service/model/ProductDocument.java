package com.catalog.catalog_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
@Setting(settingPath = "elasticsearch-settings.json")
public class ProductDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "autocomplete_search")
    private String name;

    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "autocomplete_search")
    private String description;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "autocomplete_search")
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String productPath;
} 