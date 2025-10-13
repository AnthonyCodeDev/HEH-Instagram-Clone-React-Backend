package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.LikePostUseCase;
import be.heh.stragram.application.port.in.UnlikePostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikePostUseCase likePostUseCase;
    private final UnlikePostUseCase unlikePostUseCase;
    private final PostWebMapper postWebMapper;

    @PostMapping
    public ResponseEntity<PostDtos.LikeResponse> likePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Like like = likePostUseCase.like(PostId.fromString(postId), currentUserId);
        return ResponseEntity.ok(postWebMapper.toLikeResponse(like));
    }

    @DeleteMapping
    public ResponseEntity<Void> unlikePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        unlikePostUseCase.unlike(PostId.fromString(postId), currentUserId);
        return ResponseEntity.noContent().build();
    }
}
