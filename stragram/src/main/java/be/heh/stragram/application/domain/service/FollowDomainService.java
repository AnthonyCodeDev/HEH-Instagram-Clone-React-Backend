package be.heh.stragram.application.domain.service;

import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Service;

@Service
public class FollowDomainService {
    
    public FollowRelationship createFollowRelationship(User follower, User following) {
        validateFollowOperation(follower.getId(), following.getId());
        
        follower.incrementFollowingCount();
        following.incrementFollowersCount();
        
        return FollowRelationship.create(follower.getId(), following.getId());
    }
    
    public void removeFollowRelationship(User follower, User following) {
        follower.decrementFollowingCount();
        following.decrementFollowersCount();
    }
    
    private void validateFollowOperation(UserId followerId, UserId followingId) {
        if (followerId.equals(followingId)) {
            throw new ValidationException("Users cannot follow themselves");
        }
    }
}
