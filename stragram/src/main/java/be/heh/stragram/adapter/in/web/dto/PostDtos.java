package be.heh.stragram.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

public class PostDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest {
        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        private String description;
        // The image file will be handled by MultipartFile in the controller
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostRequest {
        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResponse {
        private String id;
        private String authorId;
        private String authorUsername;
        private String authorAvatarUrl;
        private String imageUrl;
        private String description;
        private int likeCount;
        private int commentCount;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean isLikedByCurrentUser;
        private boolean isFavoritedByCurrentUser;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListResponse {
        private List<PostResponse> posts;
        private int page;
        private int size;
        private boolean hasMore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        private String postId;
        private String userId;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteResponse {
        private String postId;
        private String userId;
        private Instant createdAt;
    }
}
