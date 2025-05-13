package com.microservice_ecommerce.gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        if (ex instanceof ResponseStatusException responseStatusException) {
            response.setStatusCode(responseStatusException.getStatusCode());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        DataBufferFactory bufferFactory = response.bufferFactory();
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("path", exchange.getRequest().getURI().getPath());
        errorDetails.put("status", response.getStatusCode().value());
        errorDetails.put("error", response.getStatusCode().toString());
        errorDetails.put("message", ex.getMessage());
        
        String errorJson = "{" +
                "\"timestamp\":\"" + errorDetails.get("timestamp") + "\"," +
                "\"path\":\"" + errorDetails.get("path") + "\"," +
                "\"status\":" + errorDetails.get("status") + "," +
                "\"error\":\"" + errorDetails.get("error") + "\"," +
                "\"message\":\"" + errorDetails.get("message") + "\"" +
                "}";
        
        return response.writeWith(
                Mono.just(bufferFactory.wrap(errorJson.getBytes(StandardCharsets.UTF_8)))
        );
    }
} 