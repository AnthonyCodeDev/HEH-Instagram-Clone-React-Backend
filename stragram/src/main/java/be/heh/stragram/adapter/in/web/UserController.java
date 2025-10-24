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
