package com.catalog.catalog_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class ApiDocumentationConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog Service API")
                        .description("""
                            # Catalog Service API Documentation
                            
                            This service manages products, categories, and inventory in the e-commerce system.
                            
                            ## Authentication
                            All endpoints require authentication using JWT token in the Authorization header:
                            ```
                            Authorization: Bearer <your-jwt-token>
                            ```
                            
                            ## Common Response Codes
                            - 200: Success
                            - 201: Created
                            - 400: Bad Request
                            - 401: Unauthorized
                            - 403: Forbidden
                            - 404: Not Found
                            - 500: Internal Server Error
                            
                            ## Pagination
                            List endpoints support pagination using query parameters:
                            - page: Page number (0-based)
                            - size: Number of items per page
                            - sort: Field to sort by
                            - direction: Sort direction (asc/desc)
                            
                            ## JSON Format
                            All request and response bodies use snake_case for property names.
                            Example:
                            ```json
                            {
                              "product_id": 1,
                              "product_name": "iPhone 13",
                              "category_id": 1,
                              "image_urls": ["http://example.com/image1.jpg"]
                            }
                            ```
                            """)
                        .version("1.0")
                        .contact(new Contact()
                                .name("Catalog Service Team")
                                .email("support@catalog.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Development Server")
                ))
                .components(new Components()
                        .addSchemas("Product", new ObjectSchema()
                                .addProperty("id", new NumberSchema().description("Unique identifier").example(1))
                                .addProperty("name", new StringSchema().description("Product name").example("iPhone 13"))
                                .addProperty("description", new StringSchema().description("Product description").example("Latest iPhone model"))
                                .addProperty("price", new NumberSchema().description("Product price").example(999.99))
                                .addProperty("category_id", new NumberSchema().description("Category ID").example(1))
                                .addProperty("image_ids", new ArraySchema().items(new NumberSchema()).description("List of image IDs").example(List.of(1, 2, 3)))
                                .addProperty("image_urls", new ArraySchema().items(new StringSchema()).description("List of image URLs").example(List.of("http://example.com/image1.jpg"))))
                        .addSchemas("CreateProductRequest", new ObjectSchema()
                                .required(List.of("name", "price", "category_id"))
                                .addProperty("name", new StringSchema().description("Product name (2-100 characters)").example("iPhone 13"))
                                .addProperty("description", new StringSchema().description("Product description (max 1000 characters)").example("Latest iPhone model"))
                                .addProperty("price", new NumberSchema().description("Product price (must be positive)").example(999.99))
                                .addProperty("category_id", new NumberSchema().description("Category ID").example(1))
                                .addProperty("image_ids", new ArraySchema().items(new NumberSchema()).description("List of image IDs").example(List.of(1, 2, 3)))
                                .addProperty("image_urls", new ArraySchema().items(new StringSchema()).description("List of image URLs").example(List.of("http://example.com/image1.jpg"))))
                        .addSchemas("Category", new ObjectSchema()
                                .addProperty("id", new NumberSchema().description("Unique identifier").example(1))
                                .addProperty("name", new StringSchema().description("Category name").example("Electronics"))
                                .addProperty("description", new StringSchema().description("Category description").example("Electronic devices and accessories")))
                        .addSchemas("Inventory", new ObjectSchema()
                                .addProperty("id", new NumberSchema().description("Unique identifier").example(1))
                                .addProperty("product_id", new NumberSchema().description("Product ID").example(1))
                                .addProperty("quantity", new NumberSchema().description("Available quantity").example(100))
                                .addProperty("reserved_quantity", new NumberSchema().description("Reserved quantity").example(10)))
                        .addSchemas("PageResponse", new ObjectSchema()
                                .addProperty("content", new ArraySchema().items(new ObjectSchema()).description("List of items"))
                                .addProperty("total_elements", new NumberSchema().description("Total number of items"))
                                .addProperty("total_pages", new NumberSchema().description("Total number of pages"))
                                .addProperty("size", new NumberSchema().description("Number of items per page"))
                                .addProperty("number", new NumberSchema().description("Current page number"))
                                .addProperty("first", new StringSchema().description("Is this the first page"))
                                .addProperty("last", new StringSchema().description("Is this the last page"))
                                .addProperty("empty", new StringSchema().description("Is the content empty"))));
    }
} 