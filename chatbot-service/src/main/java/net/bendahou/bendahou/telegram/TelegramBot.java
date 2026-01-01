package net.bendahou.bendahou.telegram;

import jakarta.annotation.PostConstruct;
import net.bendahou.bendahou.agents.AIAgent;
import net.bendahou.bendahou.agents.RAGAgent;
import net.bendahou.bendahou.service.UserSessionService;
import net.bendahou.bendahou.service.UserSessionService.ChatMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    private final AIAgent aiAgent;
    private final RAGAgent ragAgent;
    private final UserSessionService sessionService;

    // Callback data constants
    private static final String CALLBACK_DATABASE = "mode_database";
    private static final String CALLBACK_POLICIES = "mode_policies";
    private static final String CALLBACK_MENU = "back_to_menu";

    public TelegramBot(AIAgent aiAgent, RAGAgent ragAgent, UserSessionService sessionService) {
        this.aiAgent = aiAgent;
        this.ragAgent = ragAgent;
        this.sessionService = sessionService;
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
            System.out.println("✅ Telegram Bot enregistré avec succès!");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Handle callback queries (button clicks)
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
                return;
            }

            // Handle text messages
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                return;
            }

            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // Handle /start command
            if (messageText.equals("/start") || messageText.equals("/menu")) {
                sendMainMenu(chatId);
                return;
            }

            // Check if user has selected a mode
            if (!sessionService.hasSelectedMode(chatId)) {
                // Send welcome message with menu for first-time users
                sendWelcomeWithMenu(chatId);
                return;
            }

            // Process message based on current mode
            sendTypingAction(chatId);
            ChatMode mode = sessionService.getUserMode(chatId);
            String answer;

            try {
                if (mode == ChatMode.DATABASE) {
                    answer = aiAgent.askAgent(messageText);
                } else {
                    answer = ragAgent.askPolicies(messageText);
                }

                if (answer == null || answer.isBlank()) {
                    answer = "⚠️ Je n'ai pas pu générer de réponse. Veuillez réessayer.";
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la génération de réponse: " + e.getMessage());
                e.printStackTrace();
                answer = "❌ Une erreur s'est produite: " + e.getMessage()
                        + "\n\nVeuillez réessayer ou taper /menu pour revenir au menu.";
            }

            sendTextMessage(chatId, answer);

        } catch (TelegramApiException e) {
            System.err.println("❌ Erreur Telegram: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        switch (data) {
            case CALLBACK_DATABASE:
                sessionService.setUserMode(chatId, ChatMode.DATABASE);
                sendTextMessage(chatId, """
                        🗄️ **Mode Base de Données activé**

                        Vous pouvez maintenant poser des questions sur:
                        • Les clients (liste, recherche, détails)
                        • Les produits (inventaire, prix, stocks)
                        • Les factures (historique, montants)

                        Tapez /menu pour revenir au menu principal.

                        Posez votre question:
                        """);
                break;

            case CALLBACK_POLICIES:
                sessionService.setUserMode(chatId, ChatMode.POLICIES);
                sendTextMessage(chatId, """
                        📋 **Mode Politiques d'Entreprise activé**

                        Je peux vous renseigner sur:
                        • Politique de retour et remboursement
                        • Conditions et frais de livraison
                        • Garanties produits
                        • Conditions générales de vente (CGV)
                        • Service client

                        Tapez /menu pour revenir au menu principal.

                        Posez votre question:
                        """);
                break;

            case CALLBACK_MENU:
                sessionService.resetUserMode(chatId);
                sendMainMenu(chatId);
                break;
        }
    }

    /**
     * Sends a personalized welcome message for first-time users or when they
     * haven't selected a mode.
     */
    private void sendWelcomeWithMenu(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText("""
                👋 **Bonjour et bienvenue!**

                Je suis *Bendahoubot*, votre assistant intelligent pour *E-Shop Maroc*.

                Je peux vous aider de deux façons:

                🗄️ *Base de Données* → Consultez les clients, produits et factures de notre système

                📋 *Politiques d'Entreprise* → Informations sur les retours, livraisons, garanties et CGV

                👇 *Choisissez un mode pour commencer:*
                """);

        // Create inline keyboard with two buttons
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // First row with two buttons
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton dbButton = new InlineKeyboardButton();
        dbButton.setText("🗄️ Base de Données");
        dbButton.setCallbackData(CALLBACK_DATABASE);
        row1.add(dbButton);

        InlineKeyboardButton policiesButton = new InlineKeyboardButton();
        policiesButton.setText("📋 Politiques");
        policiesButton.setCallbackData(CALLBACK_POLICIES);
        row1.add(policiesButton);

        rows.add(row1);
        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        execute(message);
    }

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setParseMode("Markdown");
        message.setText("""
                🤖 **Bienvenue sur Bendahoubot!**

                Je suis votre assistant intelligent pour E-Shop Maroc.

                Choisissez un mode:

                🗄️ **Base de Données** - Consultez les clients, produits et factures

                📋 **Politiques** - Informations sur les retours, livraisons, garanties
                """);

        // Create inline keyboard with two buttons
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // First row with two buttons
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton dbButton = new InlineKeyboardButton();
        dbButton.setText("🗄️ Base de Données");
        dbButton.setCallbackData(CALLBACK_DATABASE);
        row1.add(dbButton);

        InlineKeyboardButton policiesButton = new InlineKeyboardButton();
        policiesButton.setText("📋 Politiques");
        policiesButton.setCallbackData(CALLBACK_POLICIES);
        row1.add(policiesButton);

        rows.add(row1);
        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "Bendahou01bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        sendMessage.setParseMode("Markdown");
        execute(sendMessage);
    }

    private void sendTypingAction(long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}
