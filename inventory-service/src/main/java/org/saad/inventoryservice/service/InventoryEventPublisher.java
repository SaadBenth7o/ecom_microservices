package org.saad.inventoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.saad.inventoryservice.entities.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.inventory-events}")
    private String inventoryEventsTopic;

    public InventoryEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publishProductEvent(Product product, String eventType) {
        try {
            InventoryEvent event = InventoryEvent.builder()
                    .productId(String.valueOf(product.getId()))
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .eventType(eventType)
                    .timestamp(new java.util.Date())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(inventoryEventsTopic, String.valueOf(product.getId()), eventJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Inventory event published successfully for product ID: {}", product.getId());
                        } else {
                            log.error("Failed to publish inventory event for product ID: {}", product.getId(), ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing inventory event", e);
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class InventoryEvent {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private String eventType;
        private java.util.Date timestamp;
    }
}

