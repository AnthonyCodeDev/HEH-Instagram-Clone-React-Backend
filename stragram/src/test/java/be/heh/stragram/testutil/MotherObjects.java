package be.heh.stragram.testutil;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.Username;
import be.heh.stragram.application.domain.value.PasswordHash;

import java.time.Instant;
import java.util.UUID;

public final class MotherObjects {
    
    private static final Instant DEFAULT_TIME = Instant.parse("2025-10-13T12:00:00Z");
    
    public static UserBuilder user() {
        return new UserBuilder();
    }
    
    public static PostBuilder post() {
        return new PostBuilder();
    }
    
    public static CommentBuilder comment() {
        return new CommentBuilder();
    }
    
    public static class UserBuilder {
        private UserId id = UserId.of(UUID.randomUUID());
        private String email = "test@example.com";
        private String username = "testuser";
        private String passwordHash = "hashedpassword";
        private String bio = "Test bio";
        private String avatarUrl = "avatar.jpg";
        private int followersCount = 0;
        private int followingCount = 0;
        private Instant createdAt = DEFAULT_TIME;
        private Instant updatedAt = DEFAULT_TIME;
        private User.Role role = User.Role.USER;
        
        public UserBuilder withId(UUID id) {
            this.id = UserId.of(id);
            return this;
        }
        
        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }
        
        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }
        
        public UserBuilder withPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }
        
        public UserBuilder withBio(String bio) {
            this.bio = bio;
            return this;
        }
        
        public UserBuilder withAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }
        
        public UserBuilder withFollowersCount(int followersCount) {
            this.followersCount = followersCount;
            return this;
        }
        
        public UserBuilder withFollowingCount(int followingCount) {
            this.followingCount = followingCount;
            return this;
        }
        
        public UserBuilder withRole(User.Role role) {
            this.role = role;
            return this;
        }
        
        public UserBuilder asAdmin() {
            this.role = User.Role.ADMIN;
            return this;
        }
        
        public User build() {
            return User.reconstitute(
                id,
                Username.of(username),
                Email.of(email),
                PasswordHash.of(passwordHash),
                bio,
                avatarUrl,
                followersCount,
                followingCount,
                createdAt,
                updatedAt,
                role
            );
        }
    }
    
    public static class PostBuilder {
        private PostId id = PostId.of(UUID.randomUUID());
        private UserId authorId = UserId.of(UUID.randomUUID());
        private String imagePath = "image.jpg";
        private String description = "Test description";
        private int likeCount = 0;
        private int commentCount = 0;
        private Instant createdAt = DEFAULT_TIME;
        private Instant updatedAt = DEFAULT_TIME;
        
        public PostBuilder withId(UUID id) {
            this.id = PostId.of(id);
            return this;
        }
        
        public PostBuilder withAuthorId(UUID authorId) {
            this.authorId = UserId.of(authorId);
            return this;
        }
        
        public PostBuilder withImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }
        
        public PostBuilder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public PostBuilder withLikeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }
        
        public PostBuilder withCommentCount(int commentCount) {
            this.commentCount = commentCount;
            return this;
        }
        
        public Post build() {
            return Post.reconstitute(
                id,
                authorId,
                imagePath,
                description,
                likeCount,
                commentCount,
                createdAt,
                updatedAt
            );
        }
    }
    
    public static class CommentBuilder {
        private CommentId id = CommentId.of(UUID.randomUUID());
        private PostId postId = PostId.of(UUID.randomUUID());
        private UserId authorId = UserId.of(UUID.randomUUID());
        private String text = "Test comment";
        private Instant createdAt = DEFAULT_TIME;
        private Instant updatedAt = DEFAULT_TIME;
        
        public CommentBuilder withId(UUID id) {
            this.id = CommentId.of(id);
            return this;
        }
        
        public CommentBuilder withPostId(UUID postId) {
            this.postId = PostId.of(postId);
            return this;
        }
        
        public CommentBuilder withAuthorId(UUID authorId) {
            this.authorId = UserId.of(authorId);
            return this;
        }
        
        public CommentBuilder withText(String text) {
            this.text = text;
            return this;
        }
        
        public Comment build() {
            return Comment.reconstitute(
                id,
                postId,
                authorId,
                text,
                createdAt,
                updatedAt
            );
        }
    }
}
