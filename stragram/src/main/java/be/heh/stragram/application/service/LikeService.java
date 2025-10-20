package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.LikePostUseCase;
import be.heh.stragram.application.port.in.UnlikePostUseCase;
import be.heh.stragram.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService implements LikePostUseCase, UnlikePostUseCase {

    private final LoadPostPort loadPostPort;
    private final SavePostPort savePostPort;
    private final LoadUserPort loadUserPort;
    private final LikePostPort likePostPort;
    private final NotificationPort notificationPort;

    @Override
    @Transactional
    public Like like(PostId postId, UserId userId) {
        // Verify post exists
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Verify user exists
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Check if already liked
        if (likePostPort.exists(postId, userId)) {
            // Idempotent operation - return existing like
            return likePostPort.findByPostIdAndUserId(postId, userId)
                    .orElseThrow(() -> new IllegalStateException("Like exists but could not be found"));
        }

        // Create like
        Like like = Like.create(postId, userId);
        Like savedLike = likePostPort.save(like);

        // Nous n'avons plus besoin de mettre à jour le compteur de likes
        // car il est maintenant calculé à la demande

        // Create notification if the user is not the post owner
        if (!post.getAuthorId().equals(userId)) {
            User postAuthor = loadUserPort.findById(post.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("User", post.getAuthorId().toString()));

            Map<String, String> payload = new HashMap<>();
            payload.put("postId", post.getId().toString());
            payload.put("userId", user.getId().toString());
            payload.put("username", user.getUsername().toString());

            Notification notification = Notification.create(
                    postAuthor.getId(),
                    Notification.NotificationType.LIKE,
                    payload
            );

            notificationPort.save(notification);
        }

        return savedLike;
    }

    @Override
    @Transactional
    public void unlike(PostId postId, UserId userId) {
        // Verify post exists
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Check if liked
        if (!likePostPort.exists(postId, userId)) {
            // Idempotent operation - do nothing if not liked
            return;
        }

        // Delete like
        likePostPort.delete(postId, userId);

        // Nous n'avons plus besoin de mettre à jour le compteur de likes
        // car il est maintenant calculé à la demande
    }
}
