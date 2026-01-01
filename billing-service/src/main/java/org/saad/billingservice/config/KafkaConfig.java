package org.saad.billingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.billing-events}")
    private String billingEventsTopic;

    @Bean
    public NewTopic billingEventsTopic() {
        return TopicBuilder.name(billingEventsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

