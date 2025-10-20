package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final CreatePostUseCase createPostUseCase;
    private final GetPostQuery getPostQuery;
    private final ListUserPostsQuery listUserPostsQuery;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final PostWebMapper postWebMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDtos.PostResponse> createPost(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal UserId currentUserId) {
        
        try {
            CreatePostUseCase.CreatePostCommand.CreatePostCommandBuilder commandBuilder = CreatePostUseCase.CreatePostCommand.builder()
                    .authorId(currentUserId)
                    .description(description);
            
            // Ajouter l'image seulement si elle est présente
            if (image != null && !image.isEmpty()) {
                commandBuilder
                    .imageFile(image.getInputStream())
                    .originalFilename(image.getOriginalFilename())
                    .contentType(image.getContentType());
            }
            
            Post post = createPostUseCase.create(commandBuilder.build());

            return new ResponseEntity<>(postWebMapper.toPostResponse(post, currentUserId), HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDtos.PostResponse> getPost(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Post post = getPostQuery.getById(PostId.fromString(id), currentUserId);
        return ResponseEntity.ok(postWebMapper.toPostResponse(post, currentUserId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PostDtos.PostListResponse> getUserPosts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        List<Post> posts = listUserPostsQuery.listByUserId(UserId.fromString(userId), page, size);
        
        List<PostDtos.PostResponse> postResponses = posts.stream()
                .map(post -> postWebMapper.toPostResponse(post, currentUserId))
                .collect(Collectors.toList());

        PostDtos.PostListResponse response = PostDtos.PostListResponse.builder()
                .posts(postResponses)
                .page(page)
                .size(size)
                .hasMore(postResponses.size() == size)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        deletePostUseCase.delete(PostId.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }
    
    // La méthode getRecentPosts a été déplacée vers RecentPostsController
}
