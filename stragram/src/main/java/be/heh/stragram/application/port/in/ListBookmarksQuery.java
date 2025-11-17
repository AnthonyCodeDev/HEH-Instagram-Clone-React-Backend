package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface ListBookmarksQuery {
    List<Post> listBookmarkedPosts(UserId userId, int page, int size);
    boolean hasMore(UserId userId, int page, int size);
    long countBookmarks(UserId userId);
}
