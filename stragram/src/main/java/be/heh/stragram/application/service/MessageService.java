package be.heh.stragram.application.service;

import be.heh.stragram.adapter.in.websocket.dto.ConversationDeletedNotification;
import be.heh.stragram.application.domain.model.Conversation;
import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final LoadMessagesPort loadMessagesPort;
    private final SaveMessagePort saveMessagePort;
    private final LoadConversationsPort loadConversationsPort;
    private final SaveConversationPort saveConversationPort;
    private final DeleteConversationPort deleteConversationPort;
    private final LoadUserPort loadUserPort;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Message sendMessage(UserId senderId, UserId receiverId, String content) {
        // Vérifier que les utilisateurs existent
        User sender = loadUserPort.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = loadUserPort.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Trouver ou créer une conversation
        Conversation conversation = loadConversationsPort
                .findByParticipants(senderId, receiverId)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.create(Set.of(senderId, receiverId));
                    return saveConversationPort.save(newConversation);
                });

        // Si la conversation était supprimée par un des participants, la restaurer
        if (conversation.isDeletedBy(senderId)) {
            conversation.restoreFor(senderId);
        }
        if (conversation.isDeletedBy(receiverId)) {
            conversation.restoreFor(receiverId);
        }

        // Créer et sauvegarder le message
        Message message = Message.create(conversation.getId(), senderId, content);
        Message savedMessage = saveMessagePort.save(message);

        // Mettre à jour la conversation
        conversation.updateLastMessage(savedMessage.getId());
        saveConversationPort.save(conversation);

        return savedMessage;
    }

    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(ConversationId conversationId, UserId userId) {
        // Vérifier que l'utilisateur fait partie de la conversation
        Conversation conversation = loadConversationsPort.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.hasParticipant(userId)) {
            throw new IllegalArgumentException("User is not part of this conversation");
        }

        return loadMessagesPort.findByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(UserId userId) {
        return loadConversationsPort.findByParticipantId(userId).stream()
                .filter(conversation -> !conversation.isDeletedBy(userId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void markMessageAsRead(MessageId messageId, UserId userId) {
        Message message = loadMessagesPort.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // Vérifier que l'utilisateur est le destinataire
        Conversation conversation = loadConversationsPort.findById(message.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.hasParticipant(userId) || message.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("Cannot mark this message as read");
        }

        message.markAsRead();
        saveMessagePort.save(message);
    }

    @Transactional
    public void markConversationAsRead(ConversationId conversationId, UserId userId) {
        Conversation conversation = loadConversationsPort.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.hasParticipant(userId)) {
            throw new IllegalArgumentException("User is not part of this conversation");
        }

        List<Message> unreadMessages = loadMessagesPort
                .findUnreadByConversationIdAndReceiverId(conversationId, userId);

        unreadMessages.forEach(message -> {
            message.markAsRead();
            saveMessagePort.save(message);
        });
    }

    @Transactional(readOnly = true)
    public int getUnreadMessagesCount(UserId userId) {
        return loadMessagesPort.countUnreadByReceiverId(userId);
    }

    @Transactional(readOnly = true)
    public Conversation getOrCreateConversation(UserId user1Id, UserId user2Id) {
        // Vérifier que les utilisateurs existent
        loadUserPort.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        loadUserPort.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return loadConversationsPort
                .findByParticipants(user1Id, user2Id)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.create(Set.of(user1Id, user2Id));
                    return saveConversationPort.save(newConversation);
                });
    }

    @Transactional
    public void deleteConversation(ConversationId conversationId, UserId userId) {
        // Vérifier que la conversation existe
        Conversation conversation = loadConversationsPort.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.hasParticipant(userId)) {
            throw new IllegalArgumentException("User is not part of this conversation");
        }

        // Marquer la conversation comme supprimée pour cet utilisateur (soft delete)
        conversation.markAsDeletedBy(userId);
        
        // Sauvegarder les changements
        saveConversationPort.save(conversation);
    }
}
