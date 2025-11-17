package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.LikePostUseCase;
import be.heh.stragram.application.port.in.ListLikesQuery;
import be.heh.stragram.application.port.in.UnlikePostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikePostUseCase likePostUseCase;
    private final UnlikePostUseCase unlikePostUseCase;
    private final ListLikesQuery listLikesQuery;
    private final PostWebMapper postWebMapper;

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostDtos.LikeResponse> likePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Like like = likePostUseCase.like(PostId.fromString(postId), currentUserId);
        return ResponseEntity.ok(postWebMapper.toLikeResponse(like));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        unlikePostUseCase.unlike(PostId.fromString(postId), currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likes")
    public ResponseEntity<PostDtos.PostListResponse> getLikedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        List<Post> posts = listLikesQuery.listLikedPosts(currentUserId, page, size);
        boolean hasMore = listLikesQuery.hasMore(currentUserId, page, size);
        
        List<PostDtos.PostResponse> postResponses = posts.stream()
                .map(post -> postWebMapper.toPostResponse(post, currentUserId))
                .collect(Collectors.toList());
        
        PostDtos.PostListResponse response = PostDtos.PostListResponse.builder()
                .posts(postResponses)
                .page(page)
                .size(size)
                .hasMore(hasMore)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes/count")
    public ResponseEntity<Long> getLikesCount(
            @AuthenticationPrincipal UserId currentUserId) {
        long count = listLikesQuery.countLikes(currentUserId);
        return ResponseEntity.ok(count);
    }
}
