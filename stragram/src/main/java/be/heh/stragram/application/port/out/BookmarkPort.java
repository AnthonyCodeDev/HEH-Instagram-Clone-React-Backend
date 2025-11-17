package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface BookmarkPort {
    Bookmark save(Bookmark bookmark);
    void delete(UserId userId, PostId postId);
    Optional<Bookmark> findByUserIdAndPostId(UserId userId, PostId postId);
    List<Post> findBookmarkedPostsByUserId(UserId userId, int page, int size);
    boolean existsByUserIdAndPostId(UserId userId, PostId postId);
    long countByUserId(UserId userId);
}
