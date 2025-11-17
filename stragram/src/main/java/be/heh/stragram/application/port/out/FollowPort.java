package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface FollowPort {
    
    FollowRelationship save(FollowRelationship followRelationship);
    
    void delete(UserId followerId, UserId followingId);
    
    boolean exists(UserId followerId, UserId followingId);
    
    Optional<FollowRelationship> findByFollowerIdAndFollowingId(UserId followerId, UserId followingId);
    
    List<UserId> findFollowingIds(UserId followerId, int page, int size);
    
    List<UserId> findFollowerIds(UserId followingId, int page, int size);
    
    long countFollowing(UserId followerId);
    
    long countFollowers(UserId followingId);
}
