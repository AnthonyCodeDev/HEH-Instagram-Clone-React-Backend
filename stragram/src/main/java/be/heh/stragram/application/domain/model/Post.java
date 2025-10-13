package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class Post {
    private final PostId id;
    private final UserId authorId;
    private final String imagePath;
    
    @Setter(AccessLevel.PACKAGE)
    private String description;
    
    @Setter(AccessLevel.PACKAGE)
    private int likeCount;
    
    @Setter(AccessLevel.PACKAGE)
    private int commentCount;
    
    private final Instant createdAt;
    
    @Setter(AccessLevel.PACKAGE)
    private Instant updatedAt;

    private Post(
            PostId id,
            UserId authorId,
            String imagePath,
            String description,
            int likeCount,
            int commentCount,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.imagePath = imagePath;
        this.description = description;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Post create(
            UserId authorId,
            String imagePath,
            String description
    ) {
        validateDescription(description);
        
        return new Post(
                PostId.generate(),
                authorId,
                imagePath,
                description,
                0,
                0,
                Instant.now(),
                Instant.now()
        );
    }

    public static Post reconstitute(
            PostId id,
            UserId authorId,
            String imagePath,
            String description,
            int likeCount,
            int commentCount,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Post(
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

    public void updateDescription(String newDescription, UserId currentUserId) {
        if (!this.authorId.equals(currentUserId)) {
            throw new UnauthorizedActionException("Only the author can update this post");
        }
        
        validateDescription(newDescription);
        this.description = newDescription;
        this.updatedAt = Instant.now();
    }

    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = Instant.now();
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
            this.updatedAt = Instant.now();
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
        this.updatedAt = Instant.now();
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
            this.updatedAt = Instant.now();
        }
    }

    public boolean canBeDeletedBy(UserId userId, boolean isAdmin) {
        return this.authorId.equals(userId) || isAdmin;
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 2000) {
            throw new ValidationException("Description cannot exceed 2000 characters");
        }
    }
}
