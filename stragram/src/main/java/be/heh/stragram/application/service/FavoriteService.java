package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.FavoritePostUseCase;
import be.heh.stragram.application.port.in.UnfavoritePostUseCase;
import be.heh.stragram.application.port.out.FavoritePostPort;
import be.heh.stragram.application.port.out.LoadPostPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService implements FavoritePostUseCase, UnfavoritePostUseCase {

    private final LoadPostPort loadPostPort;
    private final LoadUserPort loadUserPort;
    private final FavoritePostPort favoritePostPort;

    @Override
    @Transactional
    public Favorite add(PostId postId, UserId userId) {
        // Verify post exists
        loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Check if already favorited
        if (favoritePostPort.exists(postId, userId)) {
            // Idempotent operation - return existing favorite
            return favoritePostPort.findByPostIdAndUserId(postId, userId)
                    .orElseThrow(() -> new IllegalStateException("Favorite exists but could not be found"));
        }

        // Create favorite
        Favorite favorite = Favorite.create(postId, userId);
        return favoritePostPort.save(favorite);
    }

    @Override
    @Transactional
    public void remove(PostId postId, UserId userId) {
        // Verify post exists
        loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Check if favorited
        if (!favoritePostPort.exists(postId, userId)) {
            // Idempotent operation - do nothing if not favorited
            return;
        }

        // Delete favorite
        favoritePostPort.delete(postId, userId);
    }
}
