package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
public class Notification {
    private final UUID id;
    private final UserId userId;
    private final NotificationType type;
    private final Map<String, String> payload;
    
    @Setter(AccessLevel.PACKAGE)
    private boolean read;
    
    private final Instant createdAt;

    public enum NotificationType {
        LIKE,
        COMMENT,
        FOLLOW,
        SYSTEM
    }

    private Notification(
            UUID id,
            UserId userId,
            NotificationType type,
            Map<String, String> payload,
            boolean read,
            Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.payload = payload;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static Notification create(
            UserId userId,
            NotificationType type,
            Map<String, String> payload
    ) {
        return new Notification(
                UUID.randomUUID(),
                userId,
                type,
                payload,
                false,
                Instant.now()
        );
    }

    public static Notification reconstitute(
            UUID id,
            UserId userId,
            NotificationType type,
            Map<String, String> payload,
            boolean read,
            Instant createdAt
    ) {
        return new Notification(
                id,
                userId,
                type,
                payload,
                read,
                createdAt
        );
    }

    public void markAsRead() {
        this.read = true;
    }
}
