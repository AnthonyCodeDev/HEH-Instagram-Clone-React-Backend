package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;

public interface FollowUserUseCase {
    
    FollowRelationship follow(UserId followerId, UserId targetId);
}
