package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface ListFollowingQuery {
    
    /**
     * Get the list of users that the given user follows (following)
     *
     * @param userId The user whose following we want to get
     * @param page Page number (0-indexed)
     * @param size Number of results per page
     * @return List of users that the given user follows
     */
    List<User> listFollowing(UserId userId, int page, int size);
    
    /**
     * Count total number of users that a user follows
     *
     * @param userId The user whose following we want to count
     * @return Total count of following
     */
    long countFollowing(UserId userId);
}
