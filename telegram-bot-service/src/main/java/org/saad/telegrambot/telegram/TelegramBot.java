package org.saad.telegrambot.telegram;

import jakarta.annotation.PostConstruct;
import org.saad.telegrambot.agents.AIAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Telegram Bot that integrates with AI Agent for e-commerce assistance
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    private final AIAgent aiAgent;

    public TelegramBot(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
            System.out.println("✅ Telegram Bot enregistré avec succès!");
        } catch (TelegramApiException e) {
            System.err.println("❌ Erreur lors de l'enregistrement du bot: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                return;
            }

            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            System.out.println("📩 Message de " + userName + ": " + messageText);

            // Send typing indicator
            sendTypingAction(chatId);

            // Get AI response
            String answer = aiAgent.askAgent(messageText);

            // Send response
            sendTextMessage(chatId, answer);

            System.out.println("📤 Réponse envoyée à " + userName);

        } catch (TelegramApiException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "SaadECommerceBot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        sendMessage.enableMarkdown(true);
        execute(sendMessage);
    }

    private void sendTypingAction(long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}
