package be.heh.stragram.application.domain.model.follow;

import be.heh.stragram.application.domain.value.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class FollowRelationship {
    private final UserId followerId;
    private final UserId followingId;
    private final Instant createdAt;

    private FollowRelationship(UserId followerId, UserId followingId, Instant createdAt) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = createdAt;
    }

    public static FollowRelationship create(UserId followerId, UserId followingId) {
        return new FollowRelationship(followerId, followingId, Instant.now());
    }

    public static FollowRelationship reconstitute(UserId followerId, UserId followingId, Instant createdAt) {
        return new FollowRelationship(followerId, followingId, createdAt);
    }
}
