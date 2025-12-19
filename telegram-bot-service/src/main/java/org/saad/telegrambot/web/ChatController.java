package org.saad.telegrambot.web;

import org.saad.telegrambot.agents.AIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST Controller for Chat API - accessible via Gateway
 */
@RestController
public class ChatController {

    private final AIAgent aiAgent;

    public ChatController(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam(name = "query") String query) {
        return aiAgent.askAgent(query);
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "Telegram Bot Service",
                "timestamp", LocalDateTime.now().toString());
    }

    @GetMapping("/")
    public String home() {
        return "Telegram Bot Service - E-Commerce AI Assistant ✅";
    }
}
