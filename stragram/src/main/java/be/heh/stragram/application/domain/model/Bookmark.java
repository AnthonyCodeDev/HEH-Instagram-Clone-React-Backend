package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.value.BookmarkId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Bookmark {
    private final BookmarkId id;
    private final UserId userId;
    private final PostId postId;
    private final Instant createdAt;

    public static Bookmark create(UserId userId, PostId postId) {
        return new Bookmark(
                BookmarkId.generate(),
                userId,
                postId,
                Instant.now()
        );
    }

    public static Bookmark reconstitute(
            BookmarkId id,
            UserId userId,
            PostId postId,
            Instant createdAt
    ) {
        return new Bookmark(id, userId, postId, createdAt);
    }
}
