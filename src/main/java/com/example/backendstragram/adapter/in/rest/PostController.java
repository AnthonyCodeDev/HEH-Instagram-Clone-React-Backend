package com.example.backendstragram.adapter.in.rest;

import com.example.backendstragram.config.JwtService;
import com.example.backendstragram.domain.model.Post;
import com.example.backendstragram.domain.model.User;
import com.example.backendstragram.domain.service.PostService;
import com.example.backendstragram.application.ports.in.UserUseCase;
import com.example.backendstragram.adapter.in.dto.PostResponse;
import com.example.backendstragram.adapter.in.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtService jwtService;
    private final UserUseCase userUseCase;

    private User getUserFromToken(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return userUseCase.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("caption") String caption,
            @RequestHeader("Authorization") String token
    ) {
        try {
            User user = getUserFromToken(token);
            Post post = postService.createPost(user, image, caption);
            return ResponseEntity.ok(toResponse(post, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(
            @RequestHeader("Authorization") String token
    ) {
        User user = getUserFromToken(token);
        List<PostResponse> feed = postService.getFeed().stream()
                .map(post -> toResponse(post, user))
                .collect(Collectors.toList());
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    ) {
        User currentUser = getUserFromToken(token);
        List<PostResponse> posts = postService.getUserPosts(userId).stream()
                .map(post -> toResponse(post, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token
    ) {
        User user = getUserFromToken(token);
        postService.likePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token
    ) {
        User user = getUserFromToken(token);
        postService.unlikePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    private PostResponse toResponse(Post post, User currentUser) {
        return PostResponse.builder()
                .id(post.getId())
                .imageUrl(post.getImageUrl())
                .caption(post.getCaption())
                .createdAt(post.getCreatedAt().toString())
                .user(UserResponse.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .email(post.getUser().getEmail())
                        .build())
                .likes(post.getLikes())
                .isLikedByCurrentUser(postService.hasUserLiked(post.getId(), currentUser.getId()))
                .build();
    }
}
