package org.saad.telegrambot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AI Agent that uses Spring AI ChatClient with optional MCP tools
 */
@Component
public class AIAgent {
        private final ChatClient chatClient;

        public AIAgent(ChatClient.Builder builder,
                        ChatMemory memory,
                        @Autowired(required = false) ToolCallbackProvider tools) {

                // Log available tools at startup (if any)
                if (tools != null) {
                        System.out.println("🔧 MCP Tools disponibles:");
                        Arrays.stream(tools.getToolCallbacks()).forEach(toolCallback -> {
                                System.out.println("   - " + toolCallback.getToolDefinition().name());
                        });
                } else {
                        System.out.println("⚠️ No MCP tools available - running in basic mode");
                }

                var clientBuilder = builder
                                .defaultSystem("""
                                                Vous êtes l'assistant IA de la boutique e-commerce Saad Store.

                                                Vous pouvez aider les clients avec:
                                                - Rechercher des produits
                                                - Vérifier les stocks
                                                - Consulter les informations client
                                                - Obtenir les statistiques de vente

                                                Répondez de manière professionnelle et utile en français.
                                                """)
                                .defaultAdvisors(
                                                MessageChatMemoryAdvisor.builder(memory).build());

                // Only add tools if available
                if (tools != null) {
                        clientBuilder.defaultToolCallbacks(tools);
                }

                this.chatClient = clientBuilder.build();
                System.out.println("✅ AIAgent initialisé avec succès!");
        }

        public String askAgent(String query) {
                try {
                        System.out.println("📥 Question: " + query);
                        String response = chatClient.prompt()
                                        .user(query)
                                        .call()
                                        .content();
                        System.out.println("📤 Réponse: " + (response != null
                                        ? response.substring(0, Math.min(50, response.length())) + "..."
                                        : "null"));
                        return response;
                } catch (Exception e) {
                        System.err.println(
                                        "❌ Erreur AIAgent: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                        return "Désolé, une erreur est survenue: " + e.getMessage();
                }
        }
}
