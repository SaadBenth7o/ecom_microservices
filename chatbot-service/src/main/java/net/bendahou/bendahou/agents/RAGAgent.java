package net.bendahou.bendahou.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Agent RAG (Retrieval-Augmented Generation) pour les politiques d'entreprise.
 * Répond UNIQUEMENT basé sur le contenu du fichier policies.txt.
 */
@Component
public class RAGAgent {

    private final ChatClient chatClient;
    private String policiesContent;

    @Value("classpath:policies.txt")
    private Resource policiesResource;

    public RAGAgent(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(
                        """
                                Vous êtes un assistant spécialisé dans les politiques d'entreprise d'E-Shop Maroc.

                                RÈGLES IMPORTANTES:
                                1. Répondez UNIQUEMENT aux questions concernant les politiques d'entreprise
                                2. Basez vos réponses EXCLUSIVEMENT sur le document de politiques fourni
                                3. Si la question n'est pas liée aux politiques (retour, livraison, garantie, CGV, service client),
                                   répondez poliment: "Je suis spécialisé dans les politiques d'entreprise. Pour d'autres questions,
                                   veuillez utiliser le mode 'Base de Données' ou contacter notre service client."
                                4. Ne inventez JAMAIS d'informations - utilisez uniquement ce qui est dans le document
                                5. Répondez en français, de manière claire et concise
                                6. La monnaie utilisée est le Dirham Marocain (MAD)

                                Vous pouvez répondre aux questions sur:
                                - Politique de retour et remboursement
                                - Conditions et frais de livraison
                                - Garanties produits
                                - Conditions générales de vente (CGV)
                                - Service client (horaires, contacts)
                                """)
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
     * Interroge le RAG avec la question de l'utilisateur.
     * Le contexte des politiques est inclus dans chaque requête.
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
