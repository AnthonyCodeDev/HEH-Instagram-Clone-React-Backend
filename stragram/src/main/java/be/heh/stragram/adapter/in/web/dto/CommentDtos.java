package be.heh.stragram.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

public class CommentDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCommentRequest {
        @NotBlank(message = "Comment text is required")
        @Size(max = 500, message = "Comment cannot exceed 500 characters")
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditCommentRequest {
        @NotBlank(message = "Comment text is required")
        @Size(max = 500, message = "Comment cannot exceed 500 characters")
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private String id;
        private String postId;
        private String authorId;
        private String authorUsername;
        private String authorAvatarUrl;
        private String text;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentListResponse {
        private List<CommentResponse> comments;
        private int page;
        private int size;
        private boolean hasMore;
    }
}
