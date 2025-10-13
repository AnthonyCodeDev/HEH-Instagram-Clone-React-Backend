package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.CommentDtos;
import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.dto.UserDtos;
import be.heh.stragram.adapter.in.web.mapper.CommentWebMapper;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.adapter.in.web.mapper.UserWebMapper;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final GetUserProfileQuery getUserProfileQuery;
    private final UpdateUserProfileCommand updateUserProfileCommand;
    private final DeleteUserCommand deleteUserCommand;
    private final SearchUsersQuery searchUsersQuery;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final UserWebMapper userWebMapper;
    private final PostWebMapper postWebMapper;
    private final CommentWebMapper commentWebMapper;

    @GetMapping("/users")
    public ResponseEntity<UserDtos.SearchUserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        // This is a simplified implementation - in a real app, you'd have a separate query for admin
        List<User> users = searchUsersQuery.search("", page, size);
        
        List<UserDtos.SearchUserResponseItem> userItems = users.stream()
                .map(user -> userWebMapper.toSearchUserResponseItem(user, currentUserId))
                .collect(Collectors.toList());

        UserDtos.SearchUserResponse response = UserDtos.SearchUserResponse.builder()
                .users(userItems)
                .page(page)
                .size(size)
                .hasMore(userItems.size() == size)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDtos.UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserDtos.UpdateUserRequest request,
            @AuthenticationPrincipal UserId currentUserId) {
        
        User updatedUser = updateUserProfileCommand.update(
                UserId.fromString(id),
                UpdateUserProfileCommand.UpdateProfileCommand.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .bio(request.getBio())
                        .avatarUrl(request.getAvatarUrl())
                        .build()
        );

        return ResponseEntity.ok(userWebMapper.toUserResponse(updatedUser, currentUserId));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        deleteUserCommand.delete(UserId.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDtos.PostResponse> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostDtos.UpdatePostRequest request,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Post updatedPost = updatePostUseCase.update(
                PostId.fromString(id),
                currentUserId,
                UpdatePostUseCase.UpdatePostCommand.builder()
                        .description(request.getDescription())
                        .build()
        );

        return ResponseEntity.ok(postWebMapper.toPostResponse(updatedPost, currentUserId));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        deletePostUseCase.delete(PostId.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        deleteCommentUseCase.delete(CommentId.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }
}
