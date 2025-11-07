package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;

import java.util.List;
import java.util.Optional;

public interface LoadCommentsPort {
    
    Optional<Comment> findById(CommentId id);
    
    List<Comment> findByPostId(PostId postId, int page, int size);
    
    int countByPostId(PostId postId);
}
