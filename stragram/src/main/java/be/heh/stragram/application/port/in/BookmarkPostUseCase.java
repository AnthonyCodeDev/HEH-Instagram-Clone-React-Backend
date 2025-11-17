package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

public interface BookmarkPostUseCase {
    Bookmark bookmarkPost(UserId userId, PostId postId);
    void unbookmarkPost(UserId userId, PostId postId);
}
