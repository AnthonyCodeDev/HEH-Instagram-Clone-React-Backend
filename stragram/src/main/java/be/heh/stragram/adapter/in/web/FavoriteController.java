package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.FavoritePostUseCase;
import be.heh.stragram.application.port.in.UnfavoritePostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoritePostUseCase favoritePostUseCase;
    private final UnfavoritePostUseCase unfavoritePostUseCase;
    private final PostWebMapper postWebMapper;

    @PostMapping
    public ResponseEntity<PostDtos.FavoriteResponse> favoritePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Favorite favorite = favoritePostUseCase.add(PostId.fromString(postId), currentUserId);
        return ResponseEntity.ok(postWebMapper.toFavoriteResponse(favorite));
    }

    @DeleteMapping
    public ResponseEntity<Void> unfavoritePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        unfavoritePostUseCase.remove(PostId.fromString(postId), currentUserId);
        return ResponseEntity.noContent().build();
    }
}
