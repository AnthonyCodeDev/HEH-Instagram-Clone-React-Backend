package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

@Getter
public class Conversation {
    private final ConversationId id;
    private final Set<UserId> participantIds;
    private final Instant createdAt;
    private Instant updatedAt;
    private MessageId lastMessageId;

    private Conversation(
            ConversationId id,
            Set<UserId> participantIds,
            Instant createdAt,
            Instant updatedAt,
            MessageId lastMessageId
    ) {
        this.id = id;
        this.participantIds = participantIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastMessageId = lastMessageId;
    }

    public static Conversation create(Set<UserId> participantIds) {
        if (participantIds == null || participantIds.size() != 2) {
            throw new IllegalArgumentException("A conversation must have exactly 2 participants");
        }
        
        Instant now = Instant.now();
        return new Conversation(
                ConversationId.generate(),
                participantIds,
                now,
                now,
                null
        );
    }

    public static Conversation reconstitute(
            ConversationId id,
            Set<UserId> participantIds,
            Instant createdAt,
            Instant updatedAt,
            MessageId lastMessageId
    ) {
        return new Conversation(
                id,
                participantIds,
                createdAt,
                updatedAt,
                lastMessageId
        );
    }

    public void updateLastMessage(MessageId messageId) {
        this.lastMessageId = messageId;
        this.updatedAt = Instant.now();
    }

    public boolean hasParticipant(UserId userId) {
        return participantIds.contains(userId);
    }

    public UserId getOtherParticipant(UserId userId) {
        return participantIds.stream()
                .filter(id -> !id.equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a participant"));
    }
}
