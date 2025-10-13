package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

public interface LikePostUseCase {
    
    Like like(PostId postId, UserId userId);
}
