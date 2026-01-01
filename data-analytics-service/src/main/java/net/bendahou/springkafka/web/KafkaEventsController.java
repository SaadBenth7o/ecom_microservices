package net.bendahou.springkafka.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/kafka")
@CrossOrigin(origins = "*")
@Slf4j
public class KafkaEventsController {

    private final CopyOnWriteArrayList<KafkaEvent> recentEvents = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping("/events")
    public List<KafkaEvent> getRecentEvents() {
        // Return last 100 events
        List<KafkaEvent> events = new ArrayList<>(recentEvents);
        Collections.reverse(events);
        return events.size() > 100 ? events.subList(0, 100) : events;
    }

    @GetMapping("/events/count")
    public KafkaEventStats getEventStats() {
        return new KafkaEventStats(recentEvents.size(), 
                recentEvents.stream().filter(e -> e.getTopic().equals("billing-events")).count(),
                recentEvents.stream().filter(e -> e.getTopic().equals("supplier-orders")).count(),
                0); // inventory-events removed per architecture
    }

    @GetMapping(value = "/analyticsAggregate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyticsAggregate() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Map<String, Double> map = new HashMap<>();
                
                // Compter les événements billing dans les 10 dernières secondes
                long billingCount = recentEvents.stream()
                        .filter(e -> e.getTopic().equals("billing-events"))
                        .filter(e -> System.currentTimeMillis() - e.getTimestamp() < 10000)
                        .count();
                
                // Compter les événements supplier-orders dans les 10 dernières secondes
                long supplierCount = recentEvents.stream()
                        .filter(e -> e.getTopic().equals("supplier-orders"))
                        .filter(e -> System.currentTimeMillis() - e.getTimestamp() < 10000)
                        .count();
                
                // Si aucun événement récent, on montre le total des 60 dernières secondes
                if (billingCount == 0 && supplierCount == 0) {
                    billingCount = recentEvents.stream()
                            .filter(e -> e.getTopic().equals("billing-events"))
                            .filter(e -> System.currentTimeMillis() - e.getTimestamp() < 60000)
                            .count();
                    supplierCount = recentEvents.stream()
                            .filter(e -> e.getTopic().equals("supplier-orders"))
                            .filter(e -> System.currentTimeMillis() - e.getTimestamp() < 60000)
                            .count();
                }
                
                map.put("Billing", (double) billingCount);
                map.put("Supplier", (double) supplierCount);
                
                emitter.send(SseEmitter.event()
                        .data(map)
                        .name("message"));
            } catch (IOException e) {
                log.error("Error sending SSE event", e);
                emitter.completeWithError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        emitter.onCompletion(() -> log.info("SSE connection completed"));
        emitter.onTimeout(() -> log.info("SSE connection timed out"));
        emitter.onError((ex) -> log.error("SSE connection error", ex));
        
        return emitter;
    }

    @KafkaListener(topics = {"billing-events", "supplier-orders"}, groupId = "kafka-dashboard-group")
    public void consumeEvent(@Payload String message,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        KafkaEvent event = new KafkaEvent(topic, key, message, System.currentTimeMillis());
        recentEvents.add(event);
        
        // Keep only last 1000 events
        if (recentEvents.size() > 1000) {
            recentEvents.remove(0);
        }
        
        log.debug("Received Kafka event from topic {}: {}", topic, message);
    }

    public static class KafkaEvent {
        private String topic;
        private String key;
        private String message;
        private long timestamp;

        public KafkaEvent(String topic, String key, String message, long timestamp) {
            this.topic = topic;
            this.key = key;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getTopic() { return topic; }
        public String getKey() { return key; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class KafkaEventStats {
        private long totalEvents;
        private long billingEvents;
        private long supplierEvents;
        private long inventoryEvents;

        public KafkaEventStats(long totalEvents, long billingEvents, long supplierEvents, long inventoryEvents) {
            this.totalEvents = totalEvents;
            this.billingEvents = billingEvents;
            this.supplierEvents = supplierEvents;
            this.inventoryEvents = inventoryEvents;
        }

        public long getTotalEvents() { return totalEvents; }
        public long getBillingEvents() { return billingEvents; }
        public long getSupplierEvents() { return supplierEvents; }
        public long getInventoryEvents() { return inventoryEvents; }
    }
}

