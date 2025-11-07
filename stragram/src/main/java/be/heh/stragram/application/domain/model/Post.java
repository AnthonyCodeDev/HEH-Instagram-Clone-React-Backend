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
    
    private final Instant createdAt;
    
    @Setter(AccessLevel.PACKAGE)
    private Instant updatedAt;

    private Post(
            PostId id,
            UserId authorId,
            String imagePath,
            String description,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.imagePath = imagePath;
        this.description = description;
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
                Instant.now(),
                Instant.now()
        );
    }

    public static Post reconstitute(
            PostId id,
            UserId authorId,
            String imagePath,
            String description,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Post(
                id,
                authorId,
                imagePath,
                description,
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

    // Ces méthodes ne sont plus nécessaires car nous calculons les compteurs à la demande

    public boolean canBeDeletedBy(UserId userId, boolean isAdmin) {
        return this.authorId.equals(userId) || isAdmin;
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 2000) {
            throw new ValidationException("Description cannot exceed 2000 characters");
        }
    }
}
