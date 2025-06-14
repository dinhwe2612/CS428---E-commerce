package com.catalog.catalog_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog Service API")
                        .version("1.0.0")
                        .description("All catalog endpoints: categories, inventory, products, internal APIs, sync, and reports")
                        .contact(new Contact()
                                .name("Catalog Team")
                                .email("catalog@example.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi catalogGroup() {
        return GroupedOpenApi.builder()
                .group("catalog")
                .pathsToMatch(
                        "/categories/**",
                        "/inventories/**",
                        "/products/**",
                        "/api/v1/internal/products/**",
                        "/products/sync/**",
                        "/api/v1/reports/**"
                )
                .build();
    }
}
