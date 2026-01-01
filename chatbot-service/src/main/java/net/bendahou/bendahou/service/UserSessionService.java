package net.bendahou.bendahou.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour gérer les sessions utilisateur et leur mode actif.
 * Mode DATABASE = consultation des données via MCP
 * Mode POLICIES = consultation des politiques via RAG
 */
@Service
public class UserSessionService {

    public enum ChatMode {
        DATABASE, // Mode consultation base de données (MCP)
        POLICIES // Mode consultation politiques (RAG)
    }

    private final Map<Long, ChatMode> userModes = new ConcurrentHashMap<>();

    /**
     * Récupère le mode actif pour un utilisateur.
     * Par défaut, aucun mode n'est sélectionné (retourne null).
     */
    public ChatMode getUserMode(Long chatId) {
        return userModes.get(chatId);
    }

    /**
     * Définit le mode actif pour un utilisateur.
     */
    public void setUserMode(Long chatId, ChatMode mode) {
        userModes.put(chatId, mode);
    }

    /**
     * Vérifie si l'utilisateur a sélectionné un mode.
     */
    public boolean hasSelectedMode(Long chatId) {
        return userModes.containsKey(chatId);
    }

    /**
     * Réinitialise le mode de l'utilisateur (retour au menu).
     */
    public void resetUserMode(Long chatId) {
        userModes.remove(chatId);
    }
}
