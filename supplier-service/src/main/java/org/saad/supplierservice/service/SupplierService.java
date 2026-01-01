package org.saad.supplierservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SupplierService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.supplier-orders}")
    private String supplierOrdersTopic;

    public SupplierService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processBillingEvent(String billingEvent) {
        log.info("Received billing event: {}", billingEvent);
        
        // Process the billing event and create supplier order
        String supplierOrder = processAndCreateSupplierOrder(billingEvent);
        
        // Publish supplier order to Kafka
        publishSupplierOrder(supplierOrder);
    }

    private String processAndCreateSupplierOrder(String billingEvent) {
        // Transform billing event into supplier order
        // In a real scenario, this would parse the event and create a proper order
        log.info("Processing billing event and creating supplier order");
        return "SUPPLIER_ORDER:" + billingEvent;
    }

    private void publishSupplierOrder(String supplierOrder) {
        try {
            kafkaTemplate.send(supplierOrdersTopic, supplierOrder)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Supplier order published successfully: {}", supplierOrder);
                        } else {
                            log.error("Failed to publish supplier order", ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing supplier order to Kafka", e);
        }
    }
}

