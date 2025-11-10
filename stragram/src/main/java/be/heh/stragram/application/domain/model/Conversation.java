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
    private final Set<UserId> deletedByUserIds;
    private final Instant createdAt;
    private Instant updatedAt;
    private MessageId lastMessageId;

    private Conversation(
            ConversationId id,
            Set<UserId> participantIds,
            Set<UserId> deletedByUserIds,
            Instant createdAt,
            Instant updatedAt,
            MessageId lastMessageId
    ) {
        this.id = id;
        this.participantIds = participantIds;
        this.deletedByUserIds = deletedByUserIds;
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
                new java.util.HashSet<>(),
                now,
                now,
                null
        );
    }

    public static Conversation reconstitute(
            ConversationId id,
            Set<UserId> participantIds,
            Set<UserId> deletedByUserIds,
            Instant createdAt,
            Instant updatedAt,
            MessageId lastMessageId
    ) {
        return new Conversation(
                id,
                participantIds,
                deletedByUserIds != null ? deletedByUserIds : new java.util.HashSet<>(),
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

    public void markAsDeletedBy(UserId userId) {
        if (!hasParticipant(userId)) {
            throw new IllegalArgumentException("User is not a participant");
        }
        this.deletedByUserIds.add(userId);
        this.updatedAt = Instant.now();
    }

    public boolean isDeletedBy(UserId userId) {
        return deletedByUserIds.contains(userId);
    }

    public void restoreFor(UserId userId) {
        this.deletedByUserIds.remove(userId);
        this.updatedAt = Instant.now();
    }
}
