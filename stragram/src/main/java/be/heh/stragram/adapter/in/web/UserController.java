package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.UserDtos;
import be.heh.stragram.adapter.in.web.dto.RandomUserDtos;
import be.heh.stragram.adapter.in.web.mapper.UserWebMapper;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfileQuery getUserProfileQuery;
    private final UpdateUserProfileCommand updateUserProfileCommand;
    private final DeleteUserCommand deleteUserCommand;
    private final SearchUsersQuery searchUsersQuery;
    private final be.heh.stragram.application.port.in.ListRandomUsersQuery listRandomUsersQuery;
    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final be.heh.stragram.application.port.out.ImageStoragePort imageStoragePort;
    private final be.heh.stragram.application.port.in.ChangePasswordUseCase changePasswordUseCase;
    private final UserWebMapper userWebMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDtos.UserResponse> getUserProfile(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        // Support the special "me" identifier: return the profile of the current authenticated user
        if ("me".equalsIgnoreCase(id)) {
            if (currentUserId == null) {
                return ResponseEntity.status(401).build();
            }

            User user = getUserProfileQuery.getUserById(currentUserId);
            return ResponseEntity.ok(userWebMapper.toUserResponse(user, currentUserId));
        }

        // Otherwise expect a UUID-like user id in the path
        User user = getUserProfileQuery.getUserById(UserId.fromString(id));
        return ResponseEntity.ok(userWebMapper.toUserResponse(user, currentUserId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDtos.UserResponse> getUserProfileByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserId currentUserId) {
        
        User user = getUserProfileQuery.getUserByUsername(username);
        return ResponseEntity.ok(userWebMapper.toUserResponse(user, currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDtos.UserResponse> updateUserProfile(
            @PathVariable String id,
            @Valid @RequestBody UserDtos.UpdateUserRequest request,
            @AuthenticationPrincipal UserId currentUserId) {
        
        // Security check - only allow users to update their own profile
        if (!currentUserId.toString().equals(id)) {
            return ResponseEntity.status(403).build();
        }

        User updatedUser = updateUserProfileCommand.update(
                currentUserId,
                UpdateUserProfileCommand.UpdateProfileCommand.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .bio(request.getBio())
                        .avatarUrl(request.getAvatarUrl())
                        .build()
        );

        return ResponseEntity.ok(userWebMapper.toUserResponse(updatedUser, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        // Security check - only allow users to delete their own profile
        if (!currentUserId.toString().equals(id)) {
            return ResponseEntity.status(403).build();
        }

        deleteUserCommand.delete(UserId.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<UserDtos.SearchUserResponse> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        // Authentication is optional for search. If anonymous, currentUserId will be null and mapper
        // methods should handle it.
        List<User> users = searchUsersQuery.search(query, page, size);
        
        List<UserDtos.SearchUserResponseItem> userItems = users.stream()
                .map(user -> userWebMapper.toSearchUserResponseItem(user, currentUserId))
                .collect(Collectors.toList());

        UserDtos.SearchUserResponse response = UserDtos.SearchUserResponse.builder()
                .users(userItems)
                .page(page)
                .size(size)
                .hasMore(userItems.size() == size) // Simple way to check if there are more results
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserResponse> getMyProfile(
            @AuthenticationPrincipal UserId currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = getUserProfileQuery.getUserById(currentUserId);
        return ResponseEntity.ok(userWebMapper.toUserResponse(user, currentUserId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDtos.UserResponse> updateMyProfile(
            @Valid @RequestBody UserDtos.UpdateUserRequest request,
            @AuthenticationPrincipal UserId currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        // Fetch existing user to preserve avatar/banner URLs if not provided
        User existing = getUserProfileQuery.getUserById(currentUserId);

        // Convert birthdate string to LocalDate
        java.time.LocalDate birthdate = null;
        if (request.getBirthdate() != null && !request.getBirthdate().isEmpty()) {
            try {
                birthdate = java.time.LocalDate.parse(request.getBirthdate());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null); // Invalid date format
            }
        }

        // Use request values if provided, otherwise keep existing values
        String avatarUrl = request.getAvatarUrl() != null ? request.getAvatarUrl() : existing.getAvatarUrl();
        String bannerUrl = request.getBannerUrl() != null ? request.getBannerUrl() : existing.getBannerUrl();

        User updatedUser = updateUserProfileCommand.update(
                currentUserId,
                UpdateUserProfileCommand.UpdateProfileCommand.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .bio(request.getBio())
                        .avatarUrl(avatarUrl)
                        .name(request.getName())
                        .bannerUrl(bannerUrl)
                        .phone(request.getPhone())
                        .location(request.getLocation())
                        .birthdate(birthdate)
                        .socialLinks(request.getSocialLinks())
                        .build()
        );

        return ResponseEntity.ok(userWebMapper.toUserResponse(updatedUser, currentUserId));
    }

    @PostMapping(path = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("avatar") org.springframework.web.multipart.MultipartFile avatar,
            @AuthenticationPrincipal UserId currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", "No file uploaded"));
        }

        String contentType = avatar.getContentType();
        if (!java.util.Arrays.asList("image/jpeg", "image/jpg", "image/png").contains(contentType)) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("message", "Unsupported file type"));
        }

        if (avatar.getSize() > 5 * 1024 * 1024L) {
            return ResponseEntity.status(413).body(java.util.Collections.singletonMap("message", "File too large"));
        }

        try (java.io.InputStream is = avatar.getInputStream()) {
            String stored = imageStoragePort.store(is, avatar.getOriginalFilename(), contentType);
            String avatarUrl = imageStoragePort.getImageUrl(stored);

            // Fetch existing user to reuse username/email and other fields
            User existing = getUserProfileQuery.getUserById(currentUserId);

            // Build social links map from existing user
            java.util.Map<String, String> socialLinks = new java.util.HashMap<>();
            if (existing.getTiktok() != null) socialLinks.put("tiktok", existing.getTiktok());
            if (existing.getTwitter() != null) socialLinks.put("twitter", existing.getTwitter());
            if (existing.getYoutube() != null) socialLinks.put("youtube", existing.getYoutube());

            updateUserProfileCommand.update(
                    currentUserId,
                    UpdateUserProfileCommand.UpdateProfileCommand.builder()
                            .username(existing.getUsername().toString())
                            .email(existing.getEmail().toString())
                            .bio(existing.getBio())
                            .avatarUrl(avatarUrl)
                            .name(existing.getName())
                            .bannerUrl(existing.getBannerUrl())
                            .phone(existing.getPhone())
                            .location(existing.getLocation())
                            .birthdate(existing.getBirthdate())
                            .socialLinks(socialLinks)
                            .build()
            );

            return ResponseEntity.ok(java.util.Collections.singletonMap("avatarUrl", avatarUrl));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Failed to store file"));
        }
    }

    @PostMapping("/banner")
    public ResponseEntity<?> uploadBanner(
            @RequestParam("banner") org.springframework.web.multipart.MultipartFile banner,
            @AuthenticationPrincipal UserId currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", "Unauthorized"));
        }

        // Validate file type
        String contentType = banner.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/jpg") && !contentType.equals("image/png"))) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", "Format non supporté"));
        }

        // Validate file size (5MB = 5 * 1024 * 1024 bytes)
        if (banner.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.status(413).body(java.util.Collections.singletonMap("message", "Payload Too Large"));
        }

        try (java.io.InputStream is = banner.getInputStream()) {
            String stored = imageStoragePort.store(is, banner.getOriginalFilename(), contentType);
            String bannerUrl = imageStoragePort.getImageUrl(stored);

            // Fetch existing user to reuse username/email and other fields
            User existing = getUserProfileQuery.getUserById(currentUserId);

            // Build social links map from existing user
            java.util.Map<String, String> socialLinks = new java.util.HashMap<>();
            if (existing.getTiktok() != null) socialLinks.put("tiktok", existing.getTiktok());
            if (existing.getTwitter() != null) socialLinks.put("twitter", existing.getTwitter());
            if (existing.getYoutube() != null) socialLinks.put("youtube", existing.getYoutube());

            updateUserProfileCommand.update(
                    currentUserId,
                    UpdateUserProfileCommand.UpdateProfileCommand.builder()
                            .username(existing.getUsername().toString())
                            .email(existing.getEmail().toString())
                            .bio(existing.getBio())
                            .avatarUrl(existing.getAvatarUrl())
                            .name(existing.getName())
                            .bannerUrl(bannerUrl)
                            .phone(existing.getPhone())
                            .location(existing.getLocation())
                            .birthdate(existing.getBirthdate())
                            .socialLinks(socialLinks)
                            .build()
            );

            return ResponseEntity.ok(java.util.Collections.singletonMap("bannerUrl", bannerUrl));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Internal Server Error"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody UserDtos.ChangePasswordRequest request,
            @AuthenticationPrincipal UserId currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", "Unauthorized"));
        }

        try {
            changePasswordUseCase.changePassword(currentUserId,
                    be.heh.stragram.application.port.in.ChangePasswordUseCase.ChangePasswordCommand.builder()
                            .currentPassword(request.getCurrentPassword())
                            .newPassword(request.getNewPassword())
                            .build());

            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Password changed"));
        } catch (be.heh.stragram.application.domain.exception.ValidationException e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", e.getMessage()));
        } catch (be.heh.stragram.application.domain.exception.UnauthorizedActionException e) {
            return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<UserDtos.FollowResponse> followUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        UserId targetId = UserId.fromString(id);
        
        // Can't follow yourself
        if (currentUserId.equals(targetId)) {
            return ResponseEntity.badRequest().build();
        }

        FollowRelationship followRelationship = followUserUseCase.follow(currentUserId, targetId);
        return ResponseEntity.ok(userWebMapper.toFollowResponse(followRelationship));
    }

    @GetMapping("/random")
    public ResponseEntity<RandomUserDtos.RandomUserListResponse> getRandomUsers(
        @RequestParam(defaultValue = "6") int size,
        @AuthenticationPrincipal UserId currentUserId) {

        if (size <= 0) size = 6;
        if (size > 50) size = 50; // safety cap

        // If user is authenticated, request one extra and filter them out afterwards so we still
        // return `size` other users whenever possible.
        int fetchSize = size;
        if (currentUserId != null) {
            fetchSize = Math.min(size + 1, 50);
        }

        java.util.List<be.heh.stragram.application.domain.model.User> users = listRandomUsersQuery.listRandomUsers(fetchSize);

        // Exclude current user if present
        java.util.stream.Stream<be.heh.stragram.application.domain.model.User> stream = users.stream();
        if (currentUserId != null) {
            stream = stream.filter(u -> !u.getId().equals(currentUserId));
        }

        java.util.List<RandomUserDtos.RandomUserResponse> items = stream
            .map(userWebMapper::toRandomUserResponse)
            .limit(size)
            .collect(java.util.stream.Collectors.toList());

        RandomUserDtos.RandomUserListResponse response = RandomUserDtos.RandomUserListResponse.builder()
            .users(items)
            .size(items.size())
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        UserId targetId = UserId.fromString(id);
        
        // Can't unfollow yourself
        if (currentUserId.equals(targetId)) {
            return ResponseEntity.badRequest().build();
        }

        unfollowUserUseCase.unfollow(currentUserId, targetId);
        return ResponseEntity.noContent().build();
    }
}
