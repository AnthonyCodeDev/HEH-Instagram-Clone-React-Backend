package be.heh.stragram.application.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class ConversationId {
    private final UUID value;

    private ConversationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ConversationId cannot be null");
        }
        this.value = value;
    }

    public static ConversationId of(UUID value) {
        return new ConversationId(value);
    }

    public static ConversationId of(String value) {
        return new ConversationId(UUID.fromString(value));
    }

    public static ConversationId generate() {
        return new ConversationId(UUID.randomUUID());
    }
}
