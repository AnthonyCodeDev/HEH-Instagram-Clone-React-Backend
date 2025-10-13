package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Favorite {
    private final PostId postId;
    private final UserId userId;
    private final Instant createdAt;

    private Favorite(PostId postId, UserId userId, Instant createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static Favorite create(PostId postId, UserId userId) {
        return new Favorite(postId, userId, Instant.now());
    }

    public static Favorite reconstitute(PostId postId, UserId userId, Instant createdAt) {
        return new Favorite(postId, userId, createdAt);
    }
}
