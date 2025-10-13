package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.Optional;

public interface FavoritePostPort {
    
    Favorite save(Favorite favorite);
    
    void delete(PostId postId, UserId userId);
    
    boolean exists(PostId postId, UserId userId);
    
    Optional<Favorite> findByPostIdAndUserId(PostId postId, UserId userId);
}
