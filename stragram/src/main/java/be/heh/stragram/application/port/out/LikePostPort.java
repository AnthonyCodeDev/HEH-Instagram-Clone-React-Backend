package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.Optional;

public interface LikePostPort {
    
    Like save(Like like);
    
    void delete(PostId postId, UserId userId);
    
    boolean exists(PostId postId, UserId userId);
    
    Optional<Like> findByPostIdAndUserId(PostId postId, UserId userId);
}
