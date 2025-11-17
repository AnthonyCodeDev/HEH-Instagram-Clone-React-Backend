package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface ListFollowersQuery {
    
    /**
     * Get the list of users who follow the given user (followers)
     *
     * @param userId The user whose followers we want to get
     * @param page Page number (0-indexed)
     * @param size Number of results per page
     * @return List of users who follow the given user
     */
    List<User> listFollowers(UserId userId, int page, int size);
    
    /**
     * Count total number of followers for a user
     *
     * @param userId The user whose followers we want to count
     * @return Total count of followers
     */
    long countFollowers(UserId userId);
}
