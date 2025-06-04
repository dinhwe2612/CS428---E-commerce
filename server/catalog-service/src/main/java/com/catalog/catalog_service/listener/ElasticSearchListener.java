package com.catalog.catalog_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.config.RabbitMQConfig;
import com.catalog.catalog_service.event.ProductCreatedEvent;
import com.catalog.catalog_service.model.ProductDocument;
import com.catalog.catalog_service.repository.es.ProductSearchRepository;
import com.catalog.catalog_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticSearchListener {

    private final ProductSearchRepository productSearchRepository;
    private final ProductService productService;

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_SYNC_QUEUE)
    public void handleProductSync(
            ProductCreatedEvent event,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {

        if ("product.created".equals(routingKey)) {
            ProductDocument document = ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .price(event.getPrice())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .build();
            productSearchRepository.save(document);
            log.info("Indexed product {} into Elasticsearch", event.getId());
        }
        if ("product.deleted".equals(routingKey)) {
            productSearchRepository.deleteById(event.getId());
            log.info("Deleted product {} from Elasticsearch", event.getId());
        }
        
    }
}

