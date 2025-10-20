package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface LoadPostPort {
    
    Optional<Post> findById(PostId id);
    
    List<Post> findByAuthorId(UserId authorId, int page, int size);
    
    List<Post> findByUserFeed(UserId userId, int page, int size);
    
    List<Post> findRecentPosts(int page, int size);
}
