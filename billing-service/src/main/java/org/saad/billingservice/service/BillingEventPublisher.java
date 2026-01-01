package org.saad.billingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.saad.billingservice.entities.Bill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillingEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.billing-events}")
    private String billingEventsTopic;

    public BillingEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publishBillCreated(Bill bill) {
        try {
            BillingEvent event = BillingEvent.builder()
                    .billId(bill.getId())
                    .customerId(bill.getCustomerId())
                    .billingDate(bill.getBillingDate())
                    .eventType("BILL_CREATED")
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(billingEventsTopic, String.valueOf(bill.getId()), eventJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Billing event published successfully for bill ID: {}", bill.getId());
                        } else {
                            log.error("Failed to publish billing event for bill ID: {}", bill.getId(), ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing billing event", e);
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class BillingEvent {
        private Long billId;
        private Long customerId;
        private java.util.Date billingDate;
        private String eventType;
    }
}

