package net.bendahou.bendahou.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Agent unique qui gère toutes les fonctionnalités :
 * - Base de données (MCP)
 * - Politiques d'entreprise (RAG)
 * - Analyse d'images (Vision)
 */
@Component
public class AIAgent {
    private ChatClient chatClient;
    private ChatClient.Builder chatClientBuilder; // Garder le builder pour créer des clients sans mémoire
    private ToolCallbackProvider tools;
    private String policiesContent;

    @Value("classpath:policies.txt")
    private Resource policiesResource;

    public AIAgent(ChatClient.Builder builder,
            ChatMemory memory, ToolCallbackProvider tools) {
        this.chatClientBuilder = builder;
        this.tools = tools;
        Arrays.stream(tools.getToolCallbacks()).forEach(toolCallback -> {
            System.out.println("----------------------");
            System.out.println(toolCallback.getToolDefinition());
            System.out.println("----------------------");
        });
        this.chatClient = builder
                .defaultSystem("""
                        Vous êtes un assistant IA intelligent et serviable nommé Bendahoubot.
                        Répondez aux questions de l'utilisateur de manière claire et précise.
                        Vous pouvez utiliser les outils disponibles pour obtenir des informations
                        sur les clients, produits et factures si nécessaire.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    @PostConstruct
    public void loadPolicies() {
        try {
            policiesContent = new String(policiesResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(
                    "✅ Politiques d'entreprise chargées avec succès (" + policiesContent.length() + " caractères)");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des politiques: " + e.getMessage());
            policiesContent = "Aucune politique disponible.";
        }
    }

    /**
     * Méthode principale qui accepte un Prompt (peut contenir texte, images, etc.)
     * Comme dans les fichiers de référence.
     * Si le prompt contient des images, crée un ChatClient sans mémoire pour éviter la persistance.
     */
    public String askAgent(Prompt prompt) {
        // Extraire le UserMessage du prompt
        org.springframework.ai.chat.messages.UserMessage userMessage = prompt.getInstructions().stream()
                .filter(msg -> msg instanceof org.springframework.ai.chat.messages.UserMessage)
                .map(msg -> (org.springframework.ai.chat.messages.UserMessage) msg)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prompt must contain a UserMessage"));
        
        // Vérifier si le message contient des médias (images)
        boolean hasMedia = userMessage.getMedia() != null && !userMessage.getMedia().isEmpty();
        
        if (hasMedia) {
            // Pour les images, créer un ChatClient SANS mémoire et SANS outils MCP
            // L'analyse d'images n'a pas besoin des outils MCP (getCustomers, etc.)
            // Cela évite l'erreur "multiple tools with the same name" et la persistance des images
            ChatClient imageChatClient = chatClientBuilder
                    .defaultSystem("""
                            Vous êtes un assistant qui décrit des images de manière simple et naturelle, comme un humain le ferait.
                            
                            RÈGLES IMPORTANTES:
                            - Donnez des descriptions COURTES et SIMPLES par défaut (2-3 phrases maximum)
                            - Décrivez seulement les éléments principaux que vous voyez (ex: "Je vois un stade, il y a des gens, il y a des joueurs de foot")
                            - Ne donnez pas de détails excessifs sauf si l'utilisateur demande explicitement plus de détails
                            - Utilisez un langage naturel et conversationnel, comme si vous parliez à un ami
                            - Répondez toujours en français
                            
                            Si l'utilisateur demande "décrivez-moi de plus", "plus de détails", "décrivez en détail", 
                            alors vous pouvez donner une description plus complète et détaillée.
                            """)
                    // PAS de defaultToolCallbacks - les outils MCP ne sont pas nécessaires pour l'analyse d'images
                    // PAS de MessageChatMemoryAdvisor - pour éviter de garder les images en mémoire
                    .build();
            
            // Utiliser le prompt directement (comme dans la référence)
            // Le prompt contient déjà le UserMessage avec les médias
            return imageChatClient.prompt(prompt)
                    .call()
                    .content();
        }
        
        // Pour les requêtes texte normales, utiliser le prompt tel quel avec la mémoire
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * Méthode pour les requêtes texte simples (base de données).
     */
    public String askAgent(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }

    /**
     * Méthode pour les politiques d'entreprise (RAG).
     */
    public String askPolicies(String question) {
        String prompt = String.format("""
                DOCUMENT DE POLITIQUES D'ENTREPRISE:
                =====================================
                %s
                =====================================

                QUESTION DE L'UTILISATEUR:
                %s

                INSTRUCTIONS:
                Répondez à la question en vous basant UNIQUEMENT sur le document ci-dessus.
                Si la question n'est pas liée aux politiques, indiquez-le poliment.
                """, policiesContent, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
