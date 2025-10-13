package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Like {
    private final PostId postId;
    private final UserId userId;
    private final Instant createdAt;

    private Like(PostId postId, UserId userId, Instant createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static Like create(PostId postId, UserId userId) {
        return new Like(postId, userId, Instant.now());
    }

    public static Like reconstitute(PostId postId, UserId userId, Instant createdAt) {
        return new Like(postId, userId, createdAt);
    }
}
