package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.AddCommentUseCase;
import be.heh.stragram.application.port.in.DeleteCommentUseCase;
import be.heh.stragram.application.port.in.EditCommentUseCase;
import be.heh.stragram.application.port.in.ListPostCommentsQuery;
import be.heh.stragram.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService implements AddCommentUseCase, EditCommentUseCase, 
        DeleteCommentUseCase, ListPostCommentsQuery {

    private final LoadCommentsPort loadCommentsPort;
    private final SaveCommentPort saveCommentPort;
    private final DeleteCommentPort deleteCommentPort;
    private final LoadPostPort loadPostPort;
    private final SavePostPort savePostPort;
    private final LoadUserPort loadUserPort;
    private final NotificationPort notificationPort;

    @Override
    @Transactional
    public Comment add(PostId postId, UserId authorId, AddCommentCommand command) {
        // Verify post exists
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Verify user exists
        User author = loadUserPort.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User", authorId.toString()));

        // Create comment
        Comment comment = Comment.create(postId, authorId, command.getText());
        Comment savedComment = saveCommentPort.save(comment);

        // Nous n'avons plus besoin de mettre à jour le compteur de commentaires
        // car il est maintenant calculé à la demande

        // Create notification if the author is not the post owner
        if (!post.getAuthorId().equals(authorId)) {
            User postAuthor = loadUserPort.findById(post.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("User", post.getAuthorId().toString()));

            Map<String, String> payload = new HashMap<>();
            payload.put("commentId", savedComment.getId().toString());
            payload.put("postId", post.getId().toString());
            payload.put("authorId", author.getId().toString());
            payload.put("authorUsername", author.getUsername().toString());
            payload.put("commentText", savedComment.getText());

            Notification notification = Notification.create(
                    postAuthor.getId(),
                    Notification.NotificationType.COMMENT,
                    payload
            );

            notificationPort.save(notification);
        }

        return savedComment;
    }

    @Override
    @Transactional
    public Comment edit(CommentId commentId, UserId requesterId, EditCommentCommand command) {
        Comment comment = loadCommentsPort.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment", commentId.toString()));

        comment.updateText(command.getText(), requesterId);
        
        return saveCommentPort.save(comment);
    }

    @Override
    @Transactional
    public void delete(CommentId commentId, UserId requesterId) {
        Comment comment = loadCommentsPort.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment", commentId.toString()));

        User requester = loadUserPort.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User", requesterId.toString()));

        if (!comment.canBeDeletedBy(requesterId, requester.isAdmin())) {
            throw new UnauthorizedActionException("You don't have permission to delete this comment");
        }

        // Nous n'avons plus besoin de mettre à jour le compteur de commentaires
        // car il est maintenant calculé à la demande

        // Delete comment
        deleteCommentPort.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> list(PostId postId, int page, int size) {
        // Verify post exists
        loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        return loadCommentsPort.findByPostId(postId, page, size);
    }
}
