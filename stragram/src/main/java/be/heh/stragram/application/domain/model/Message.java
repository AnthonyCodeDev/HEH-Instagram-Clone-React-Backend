package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Message {
    private final MessageId id;
    private final ConversationId conversationId;
    private final UserId senderId;
    private final String content;
    private final Instant sentAt;
    private boolean isRead;
    private Instant readAt;

    private Message(
            MessageId id,
            ConversationId conversationId,
            UserId senderId,
            String content,
            Instant sentAt,
            boolean isRead,
            Instant readAt
    ) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
        this.readAt = readAt;
    }

    public static Message create(
            ConversationId conversationId,
            UserId senderId,
            String content
    ) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Message content cannot exceed 1000 characters");
        }
        
        return new Message(
                MessageId.generate(),
                conversationId,
                senderId,
                content.trim(),
                Instant.now(),
                false,
                null
        );
    }

    public static Message reconstitute(
            MessageId id,
            ConversationId conversationId,
            UserId senderId,
            String content,
            Instant sentAt,
            boolean isRead,
            Instant readAt
    ) {
        return new Message(
                id,
                conversationId,
                senderId,
                content,
                sentAt,
                isRead,
                readAt
        );
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
        }
    }
}
