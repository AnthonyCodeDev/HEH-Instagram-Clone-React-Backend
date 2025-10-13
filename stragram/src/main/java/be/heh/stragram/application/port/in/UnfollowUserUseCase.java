package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.UserId;

public interface UnfollowUserUseCase {
    
    void unfollow(UserId followerId, UserId targetId);
}
