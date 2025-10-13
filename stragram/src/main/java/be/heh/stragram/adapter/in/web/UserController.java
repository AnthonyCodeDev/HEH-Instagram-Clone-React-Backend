package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.UserDtos;
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
    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final UserWebMapper userWebMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDtos.UserResponse> getUserProfile(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
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
