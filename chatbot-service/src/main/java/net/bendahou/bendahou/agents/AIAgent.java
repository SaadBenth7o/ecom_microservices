package net.bendahou.bendahou.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AIAgent {
    private ChatClient chatClient;

    public AIAgent(ChatClient.Builder builder,
            ChatMemory memory, ToolCallbackProvider tools) {
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
                        sur les employés si nécessaire.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build())
                .defaultToolCallbacks(tools)

                .build();
    }

    public String askAgent(String query) {
        return chatClient.prompt()
                .user(query)
                .call().content();
    }
}
