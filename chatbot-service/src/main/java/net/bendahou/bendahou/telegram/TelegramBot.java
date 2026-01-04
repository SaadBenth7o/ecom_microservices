package net.bendahou.bendahou.telegram;

import jakarta.annotation.PostConstruct;
import net.bendahou.bendahou.agents.AIAgent;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import net.bendahou.bendahou.service.UserSessionService;
import net.bendahou.bendahou.service.UserSessionService.ChatMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    private final AIAgent aiAgent;
    private final UserSessionService sessionService;

    // Callback data constants
    private static final String CALLBACK_DATABASE = "mode_database";
    private static final String CALLBACK_POLICIES = "mode_policies";
    private static final String CALLBACK_IMAGE_ANALYSIS = "mode_image_analysis";
    private static final String CALLBACK_MENU = "back_to_menu";

    public TelegramBot(AIAgent aiAgent, UserSessionService sessionService) {
        this.aiAgent = aiAgent;
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

            if (!update.hasMessage()) {
                return;
            }

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            // Handle photos (images)
            if (message.hasPhoto()) {
                handlePhotoMessage(message, chatId);
                return;
            }

            // Handle text messages
            if (!message.hasText()) {
                return;
            }

            String messageText = message.getText();

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
                } else if (mode == ChatMode.POLICIES) {
                    answer = aiAgent.askPolicies(messageText);
                } else {
                    // IMAGE_ANALYSIS mode - but user sent text, not image
                    answer = "📷 Veuillez envoyer une image pour l'analyser. " +
                            "Vous pouvez également envoyer une image avec une légende pour poser une question spécifique.";
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

    /**
     * Gère les messages contenant des photos (images).
     * Suit la logique des fichiers de référence : crée un UserMessage avec Media et passe un Prompt à AIAgent.
     */
    private void handlePhotoMessage(Message message, Long chatId) throws TelegramApiException {
        try {
            // Vérifier si l'utilisateur est en mode analyse d'images
            ChatMode mode = sessionService.getUserMode(chatId);
            if (mode != ChatMode.IMAGE_ANALYSIS) {
                sendTextMessage(chatId, """
                        📷 **Image reçue**
                        
                        Pour analyser cette image, veuillez d'abord activer le mode "Analyse d'Images" 
                        en tapant /menu et en sélectionnant le bouton correspondant.
                        """);
                return;
            }

            // Envoyer une action "typing" pour indiquer que le bot traite l'image
            sendTypingAction(chatId);

            // Récupérer les photos
            List<PhotoSize> photos = message.getPhoto();
            List<Media> mediaList = new ArrayList<>();
            String caption = message.getCaption();
            
            // Si pas de légende, utiliser un prompt par défaut pour analyse d'images
            String query = caption != null && !caption.isBlank() 
                    ? caption 
                    : "Qu'est-ce que vous voyez dans cette image ? Décrivez de manière simple et naturelle, comme un humain le ferait (2-3 phrases maximum).";

            // Traiter toutes les photos (comme dans la référence)
            for (PhotoSize ps : photos) {
                String fileId = ps.getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                File file = execute(getFile);
                String filePath = file.getFilePath();
                String textUrl = "https://api.telegram.org/file/bot"
                        + telegramBotToken + "/" + filePath;
                java.net.URL fileUrl = new java.net.URL(textUrl);
                mediaList.add(Media.builder()
                        .id(fileId)
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                        .data(new UrlResource(fileUrl))
                        .build());
            }

            // Créer un UserMessage avec le texte et les médias (comme dans la référence)
            UserMessage userMessage = UserMessage.builder()
                    .text(query)
                    .media(mediaList)
                    .build();

            // Passer le Prompt à AIAgent (comme dans la référence)
            String answer = aiAgent.askAgent(new Prompt(userMessage));

            // Envoyer la réponse en texte brut (sans Markdown) pour éviter les erreurs de parsing
            sendPlainTextMessage(chatId, answer);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du traitement de l'image: " + e.getMessage());
            e.printStackTrace();
            sendPlainTextMessage(chatId, "❌ Une erreur s'est produite lors de l'analyse de l'image: " + e.getMessage());
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

            case CALLBACK_IMAGE_ANALYSIS:
                sessionService.setUserMode(chatId, ChatMode.IMAGE_ANALYSIS);
                sendTextMessage(chatId, """
                        📷 **Mode Analyse d'Images activé**

                        Envoyez-moi une image et je vais la décrire en détail !

                        Vous pouvez:
                        • Envoyer une image seule → Je la décrirai automatiquement
                        • Envoyer une image avec une légende → Je répondrai à votre question spécifique
                        • Poser des questions comme "Qu'est-ce que vous voyez ?" ou "Décrivez-moi cette image"

                        Tapez /menu pour revenir au menu principal.

                        Envoyez votre image:
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

                Je peux vous aider de trois façons:

                🗄️ *Base de Données* → Consultez les clients, produits et factures de notre système

                📋 *Politiques d'Entreprise* → Informations sur les retours, livraisons, garanties et CGV

                📷 *Analyse d'Images* → Décrivez et analysez des images avec l'IA

                👇 *Choisissez un mode pour commencer:*
                """);

        // Create inline keyboard with three buttons
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

        // Second row with image analysis button
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton imageButton = new InlineKeyboardButton();
        imageButton.setText("📷 Analyse d'Images");
        imageButton.setCallbackData(CALLBACK_IMAGE_ANALYSIS);
        row2.add(imageButton);

        rows.add(row2);
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

                📷 **Analyse d'Images** - Décrivez et analysez des images avec l'IA
                """);

        // Create inline keyboard with three buttons
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

        // Second row with image analysis button
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton imageButton = new InlineKeyboardButton();
        imageButton.setText("📷 Analyse d'Images");
        imageButton.setCallbackData(CALLBACK_IMAGE_ANALYSIS);
        row2.add(imageButton);

        rows.add(row2);
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

    /**
     * Envoie un message en texte brut (sans parsing Markdown).
     * Utilisé pour les réponses d'analyse d'images qui peuvent contenir des caractères spéciaux.
     */
    private void sendPlainTextMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        // Pas de setParseMode = texte brut, pas de parsing Markdown
        execute(sendMessage);
    }

    private void sendTypingAction(long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}
