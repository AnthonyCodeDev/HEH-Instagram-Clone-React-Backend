package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.PostId;

import java.util.List;

public interface ListPostCommentsQuery {
    
    List<Comment> list(PostId postId, int page, int size);
}
