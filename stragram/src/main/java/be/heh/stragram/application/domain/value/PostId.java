package be.heh.stragram.application.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PostId {
    private final UUID value;

    private PostId(UUID value) {
        this.value = value;
    }

    public static PostId of(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }
        return new PostId(id);
    }

    public static PostId fromString(String id) {
        try {
            return new PostId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid post ID format", e);
        }
    }

    public static PostId generate() {
        return new PostId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
