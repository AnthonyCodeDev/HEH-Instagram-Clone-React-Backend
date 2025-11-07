package be.heh.stragram.application.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class MessageId {
    private final UUID value;

    private MessageId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("MessageId cannot be null");
        }
        this.value = value;
    }

    public static MessageId of(UUID value) {
        return new MessageId(value);
    }

    public static MessageId of(String value) {
        return new MessageId(UUID.fromString(value));
    }

    public static MessageId generate() {
        return new MessageId(UUID.randomUUID());
    }
}
