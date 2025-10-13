package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

public interface UnlikePostUseCase {
    
    void unlike(PostId postId, UserId userId);
}
