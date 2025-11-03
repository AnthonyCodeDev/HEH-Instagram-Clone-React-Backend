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
import be.heh.stragram.application.port.out.DeleteCommentPort;
import be.heh.stragram.application.port.out.LoadCommentsPort;
import be.heh.stragram.application.port.out.LoadPostPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.NotificationPort;
import be.heh.stragram.application.port.out.SaveCommentPort;
import be.heh.stragram.application.port.out.SavePostPort;
import be.heh.stragram.testutil.MotherObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private LoadCommentsPort loadCommentsPort;

    @Mock
    private SaveCommentPort saveCommentPort;

    @Mock
    private DeleteCommentPort deleteCommentPort;

    @Mock
    private LoadPostPort loadPostPort;

    @Mock
    private SavePostPort savePostPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private NotificationPort notificationPort;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(loadCommentsPort, saveCommentPort, deleteCommentPort, loadPostPort, savePostPort, loadUserPort, notificationPort);
    }

    @Test
    void add_creates_comment_and_saves_notification_when_author_not_post_owner() {
        User author = MotherObjects.user().withUsername("commenter").build();
        Post post = MotherObjects.post().build();

        when(loadPostPort.findById(post.getId())).thenReturn(Optional.of(post));
        when(loadUserPort.findById(author.getId())).thenReturn(Optional.of(author));

        Comment saved = MotherObjects.comment().withPostId(post.getId().getValue()).withAuthorId(author.getId().getValue()).withText("hello").build();
        when(saveCommentPort.save(any(Comment.class))).thenReturn(saved);

        when(loadUserPort.findById(post.getAuthorId())).thenReturn(Optional.of(MotherObjects.user().withId(post.getAuthorId().getValue()).build()));

    var result = commentService.add(post.getId(), author.getId(), be.heh.stragram.application.port.in.AddCommentUseCase.AddCommentCommand.builder().text("hello").build());

        assertThat(result).isNotNull();
        verify(saveCommentPort).save(any(Comment.class));
        verify(notificationPort).save(any(Notification.class));
    }

    @Test
    void delete_throws_when_not_allowed() {
        Comment comment = MotherObjects.comment().build();
        User requester = MotherObjects.user().withId(java.util.UUID.randomUUID()).build(); // different id

        when(loadCommentsPort.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(loadUserPort.findById(requester.getId())).thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> commentService.delete(comment.getId(), requester.getId()))
                .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void edit_updates_and_saves_comment() {
        Comment comment = MotherObjects.comment().build();
        when(loadCommentsPort.findById(comment.getId())).thenReturn(Optional.of(comment));

        Comment updated = MotherObjects.comment().withId(comment.getId().getValue()).withText("new").build();
        when(saveCommentPort.save(any(Comment.class))).thenReturn(updated);

    var result = commentService.edit(comment.getId(), comment.getAuthorId(), be.heh.stragram.application.port.in.EditCommentUseCase.EditCommentCommand.builder().text("new").build());

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("new");
        verify(saveCommentPort).save(any(Comment.class));
    }

    @Test
    void list_throws_when_post_not_found() {
        PostId postId = PostId.of(java.util.UUID.randomUUID());
        when(loadPostPort.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.list(postId, 0, 10))
                .isInstanceOf(NotFoundException.class);
    }
}
