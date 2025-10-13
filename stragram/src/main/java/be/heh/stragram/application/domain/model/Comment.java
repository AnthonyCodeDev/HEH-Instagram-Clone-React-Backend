package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class Comment {
    private final CommentId id;
    private final PostId postId;
    private final UserId authorId;
    
    @Setter(AccessLevel.PACKAGE)
    private String text;
    
    private final Instant createdAt;
    
    @Setter(AccessLevel.PACKAGE)
    private Instant updatedAt;

    private Comment(
            CommentId id,
            PostId postId,
            UserId authorId,
            String text,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Comment create(
            PostId postId,
            UserId authorId,
            String text
    ) {
        validateText(text);
        
        return new Comment(
                CommentId.generate(),
                postId,
                authorId,
                text,
                Instant.now(),
                Instant.now()
        );
    }

    public static Comment reconstitute(
            CommentId id,
            PostId postId,
            UserId authorId,
            String text,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Comment(
                id,
                postId,
                authorId,
                text,
                createdAt,
                updatedAt
        );
    }

    public void updateText(String newText, UserId currentUserId) {
        if (!this.authorId.equals(currentUserId)) {
            throw new UnauthorizedActionException("Only the author can update this comment");
        }
        
        validateText(newText);
        this.text = newText;
        this.updatedAt = Instant.now();
    }

    public boolean canBeDeletedBy(UserId userId, boolean isAdmin) {
        return this.authorId.equals(userId) || isAdmin;
    }

    private static void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException("Comment text cannot be empty");
        }
        
        if (text.length() > 500) {
            throw new ValidationException("Comment text cannot exceed 500 characters");
        }
    }
}
