package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface ListUserPostsQuery {
    
    List<Post> listByUserId(UserId userId, int page, int size);
}
