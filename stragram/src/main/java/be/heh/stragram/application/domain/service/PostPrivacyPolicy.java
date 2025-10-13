package be.heh.stragram.application.domain.service;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Service;

/**
 * Service to handle post visibility rules.
 * Currently, all posts are public, but this service can be extended
 * in the future to support private posts, followers-only posts, etc.
 */
@Service
public class PostPrivacyPolicy {
    
    public boolean canViewPost(Post post, UserId viewerId) {
        // Currently all posts are public
        return true;
    }
    
    public boolean canEditPost(Post post, UserId editorId) {
        return post.getAuthorId().equals(editorId);
    }
    
    public boolean canDeletePost(Post post, User user) {
        return post.getAuthorId().equals(user.getId()) || user.isAdmin();
    }
}
