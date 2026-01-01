package org.saad.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.inventory-events}")
    private String inventoryEventsTopic;

    @Bean
    public NewTopic inventoryEventsTopic() {
        return TopicBuilder.name(inventoryEventsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

