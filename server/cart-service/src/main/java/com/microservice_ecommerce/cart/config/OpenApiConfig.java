package com.microservice_ecommerce.cart.config;

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
    public OpenAPI cartServiceOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8085") // Gateway address
                .description("Gateway");
        return new OpenAPI()
                .servers(List.of(gatewayServer))
                .info(new Info()
                        .title("Cart Service API")
                        .version("1.0.0")
                        .description("Endpoints for retrieving and modifying the shopping cart")
                        .contact(new Contact()
                                .name("Cart Service Team")
                                .email("cart@example.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi cartGroup() {
        return GroupedOpenApi.builder()
                .group("cart")
                .pathsToMatch("/api/v1/cart/**")
                .build();
    }
}
