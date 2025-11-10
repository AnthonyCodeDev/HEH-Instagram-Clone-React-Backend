package be.heh.stragram.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class UserDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String username;
        private String name;
        private String email;
        private String bio;
        private String avatarUrl;
        private String bannerUrl;
        private String phone;
        private String location;
        private String birthdate;       // format: YYYY-MM-DD
        private Map<String, String> socialLinks;
        private int followersCount;
        private int followingCount;
        private Instant createdAt;
        private boolean isCurrentUserFollowing;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username can only contain letters, numbers, dots, and underscores")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        private String bio;

        private String avatarUrl;
        private String name;
        private String bannerUrl;
        @Size(max = 30, message = "Phone cannot exceed 30 characters")
        private String phone;
        @Size(max = 100, message = "Location cannot exceed 100 characters")
        private String location;
        private String birthdate;       // format: YYYY-MM-DD
        private Map<String, String> socialLinks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchUserResponseItem {
        private String id;
        private String username;
        private String avatarUrl;
        private int followersCount;
        private int followingCount;
        private boolean isCurrentUserFollowing;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchUserResponse {
        private List<SearchUserResponseItem> users;
        private int page;
        private int size;
        private boolean hasMore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowResponse {
        private String followerId;
        private String followingId;
        private Instant createdAt;
    }
}
