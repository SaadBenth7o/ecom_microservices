package org.saad.supplierservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.saad.supplierservice.service.SupplierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BillingEventListener {

    private final SupplierService supplierService;

    @Value("${kafka.topic.billing-events}")
    private String billingEventsTopic;

    public BillingEventListener(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @KafkaListener(topics = "${kafka.topic.billing-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBillingEvent(String message) {
        log.info("Received billing event from Kafka: {}", message);
        supplierService.processBillingEvent(message);
    }
}

