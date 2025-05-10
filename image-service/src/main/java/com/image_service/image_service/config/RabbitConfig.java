package com.image_service.image_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  public static final String EXCHANGE = "image.exchange";
  public static final String UPLOAD_QUEUE = "image.upload.queue";
  public static final String ROUTING_KEY = "image.upload";

  @Bean
  Exchange imageExchange() {
    return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
  }

  @Bean
  Queue uploadQueue() {
    return QueueBuilder.durable(UPLOAD_QUEUE).build();
  }

  @Bean
  Binding binding(Queue uploadQueue, Exchange imageExchange) {
    return BindingBuilder.bind(uploadQueue)
        .to(imageExchange)
        .with(ROUTING_KEY)
        .noargs();
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
    RabbitTemplate rt = new RabbitTemplate(cf);
    rt.setMessageConverter(jsonMessageConverter());
    return rt;
  }
}
