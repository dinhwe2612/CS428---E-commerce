package com.catalog.catalog_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogServiceOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8085") // Gateway address
                .description("Gateway");
        return new OpenAPI()
                .servers(List.of(gatewayServer))
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
