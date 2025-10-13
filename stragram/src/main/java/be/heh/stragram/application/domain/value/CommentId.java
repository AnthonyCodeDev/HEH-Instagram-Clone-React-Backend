package be.heh.stragram.application.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class CommentId {
    private final UUID value;

    private CommentId(UUID value) {
        this.value = value;
    }

    public static CommentId of(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Comment ID cannot be null");
        }
        return new CommentId(id);
    }

    public static CommentId fromString(String id) {
        try {
            return new CommentId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid comment ID format", e);
        }
    }

    public static CommentId generate() {
        return new CommentId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
