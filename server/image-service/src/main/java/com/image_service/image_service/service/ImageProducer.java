package com.image_service.image_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.image_service.image_service.config.RabbitConfig;

import java.util.Map;

@Service
public class ImageProducer {
  
  private final RabbitTemplate rabbitTemplate;
  
  @Autowired
  public ImageProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }
  
  public void sendImageToQueue(Map<String, Object> imageData) {
    rabbitTemplate.convertAndSend(
        RabbitConfig.EXCHANGE,
        RabbitConfig.ROUTING_KEY,
        imageData);
  }
}
