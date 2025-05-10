package com.image_service.image_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.image_service.image_service.DTOs.ImageUploadRequest;
import com.image_service.image_service.config.RabbitConfig;

import java.nio.file.Path;

@Service
public class ImageProducer {
  private final RabbitTemplate template;

  public ImageProducer(RabbitTemplate template) {
    this.template = template;
  }

  public void sendUploadRequest(Path tempFile) {
    ImageUploadRequest msg = new ImageUploadRequest();
    msg.setTempFilePath(tempFile.toString());

    template.convertAndSend(RabbitConfig.EXCHANGE,
        RabbitConfig.ROUTING_KEY,
        msg);
  }
}
