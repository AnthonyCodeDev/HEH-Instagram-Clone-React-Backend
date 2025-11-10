package be.heh.stragram.adapter.in.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO envoyé via WebSocket quand une conversation est supprimée.
 * Permet au frontend de mettre à jour l'UI en temps réel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDeletedNotification {
    /**
     * UUID de la conversation qui a été supprimée
     */
    private UUID conversationId;
    
    /**
     * UUID de l'utilisateur qui a supprimé la conversation
     */
    private UUID deletedBy;
}
