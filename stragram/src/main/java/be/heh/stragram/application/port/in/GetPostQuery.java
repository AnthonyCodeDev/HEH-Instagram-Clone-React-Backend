package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

public interface GetPostQuery {
    
    Post getById(PostId postId, UserId requesterId);
}
