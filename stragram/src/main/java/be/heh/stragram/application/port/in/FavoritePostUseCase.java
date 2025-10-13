package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;

public interface FavoritePostUseCase {
    
    Favorite add(PostId postId, UserId userId);
}
