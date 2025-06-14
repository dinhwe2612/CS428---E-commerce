package com.image_service.image_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI imageServiceOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8085") // Gateway address
                .description("Gateway");
        return new OpenAPI()
                .servers(List.of(gatewayServer))
                .info(new Info()
                        .title("Image Service API")
                        .description("APIs for uploading and retrieving images")
                )
                .components(new Components());
    }

    @Bean
    public GroupedOpenApi imagesGroup() {
        return GroupedOpenApi.builder()
                .group("images")
                .pathsToMatch("/api/v1/images/**")
                .build();
    }
}
