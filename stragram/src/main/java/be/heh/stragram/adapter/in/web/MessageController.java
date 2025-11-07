package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.ConversationDto;
import be.heh.stragram.adapter.in.web.dto.MessageDto;
import be.heh.stragram.application.domain.model.Conversation;
import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LoadMessagesPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final LoadUserPort loadUserPort;
    private final LoadMessagesPort loadMessagesPort;

    /**
     * Récupérer toutes les conversations de l'utilisateur connecté
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(Authentication authentication) {
        UserId userId = getUserIdFromAuth(authentication);
        
        List<Conversation> conversations = messageService.getUserConversations(userId);
        
        List<ConversationDto> conversationDtos = conversations.stream()
                .map(conversation -> {
                    UserId otherUserId = conversation.getOtherParticipant(userId);
                    User otherUser = loadUserPort.findById(otherUserId).orElse(null);
                    
                    MessageDto lastMessageDto = null;
                    if (conversation.getLastMessageId() != null) {
                        Message lastMessage = loadMessagesPort.findById(conversation.getLastMessageId())
                                .orElse(null);
                        if (lastMessage != null) {
                            User sender = loadUserPort.findById(lastMessage.getSenderId()).orElse(null);
                            lastMessageDto = toMessageDto(lastMessage, sender);
                        }
                    }
                    
                    // Compter les messages non lus
                    int unreadCount = loadMessagesPort
                            .findUnreadByConversationIdAndReceiverId(conversation.getId(), userId)
                            .size();
                    
                    return ConversationDto.builder()
                            .id(conversation.getId().getValue())
                            .otherUserId(otherUser != null ? otherUser.getId().getValue() : null)
                            .otherUserUsername(otherUser != null ? otherUser.getUsername().toString() : "Unknown")
                            .otherUserAvatarUrl(otherUser != null ? otherUser.getAvatarUrl() : null)
                            .lastMessage(lastMessageDto)
                            .unreadCount(unreadCount)
                            .createdAt(conversation.getCreatedAt())
                            .updatedAt(conversation.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(conversationDtos);
    }

    /**
     * Récupérer ou créer une conversation avec un utilisateur
     */
    @GetMapping("/conversations/with/{otherUserId}")
    public ResponseEntity<ConversationDto> getOrCreateConversation(
            @PathVariable UUID otherUserId,
            Authentication authentication
    ) {
        UserId userId = getUserIdFromAuth(authentication);
        UserId otherUserIdValue = UserId.of(otherUserId);
        
        Conversation conversation = messageService.getOrCreateConversation(userId, otherUserIdValue);
        User otherUser = loadUserPort.findById(otherUserIdValue)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        MessageDto lastMessageDto = null;
        if (conversation.getLastMessageId() != null) {
            Message lastMessage = loadMessagesPort.findById(conversation.getLastMessageId())
                    .orElse(null);
            if (lastMessage != null) {
                User sender = loadUserPort.findById(lastMessage.getSenderId()).orElse(null);
                lastMessageDto = toMessageDto(lastMessage, sender);
            }
        }
        
        int unreadCount = loadMessagesPort
                .findUnreadByConversationIdAndReceiverId(conversation.getId(), userId)
                .size();
        
        ConversationDto dto = ConversationDto.builder()
                .id(conversation.getId().getValue())
                .otherUserId(otherUser.getId().getValue())
                .otherUserUsername(otherUser.getUsername().toString())
                .otherUserAvatarUrl(otherUser.getAvatarUrl())
                .lastMessage(lastMessageDto)
                .unreadCount(unreadCount)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Récupérer tous les messages d'une conversation
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getConversationMessages(
            @PathVariable UUID conversationId,
            Authentication authentication
    ) {
        UserId userId = getUserIdFromAuth(authentication);
        ConversationId convId = ConversationId.of(conversationId);
        
        List<Message> messages = messageService.getConversationMessages(convId, userId);
        
        List<MessageDto> messageDtos = messages.stream()
                .map(message -> {
                    User sender = loadUserPort.findById(message.getSenderId()).orElse(null);
                    return toMessageDto(message, sender);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(messageDtos);
    }

    /**
     * Marquer tous les messages d'une conversation comme lus
     */
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable UUID conversationId,
            Authentication authentication
    ) {
        UserId userId = getUserIdFromAuth(authentication);
        ConversationId convId = ConversationId.of(conversationId);
        
        messageService.markConversationAsRead(convId, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Obtenir le nombre de messages non lus
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(Authentication authentication) {
        UserId userId = getUserIdFromAuth(authentication);
        int unreadCount = messageService.getUnreadMessagesCount(userId);
        return ResponseEntity.ok(unreadCount);
    }

    /**
     * Supprimer une conversation
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable UUID conversationId,
            Authentication authentication
    ) {
        UserId userId = getUserIdFromAuth(authentication);
        ConversationId convId = ConversationId.of(conversationId);
        
        messageService.deleteConversation(convId, userId);
        
        return ResponseEntity.noContent().build();
    }

    private UserId getUserIdFromAuth(Authentication authentication) {
        String userIdStr = authentication.getName();
        return UserId.of(UUID.fromString(userIdStr));
    }

    private MessageDto toMessageDto(Message message, User sender) {
        return MessageDto.builder()
                .id(message.getId().getValue())
                .conversationId(message.getConversationId().getValue())
                .senderId(message.getSenderId().getValue())
                .senderUsername(sender != null ? sender.getUsername().toString() : "Unknown")
                .senderAvatarUrl(sender != null ? sender.getAvatarUrl() : null)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .build();
    }
}
