package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.CreatePostUseCase;
import be.heh.stragram.application.port.in.GetPostQuery;
import be.heh.stragram.application.port.out.DeletePostPort;
import be.heh.stragram.application.port.out.ImageStoragePort;
import be.heh.stragram.application.port.out.LoadPostPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.SavePostPort;
import be.heh.stragram.testutil.MotherObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTests {

    @Mock
    private LoadPostPort loadPostPort;

    @Mock
    private SavePostPort savePostPort;

    @Mock
    private DeletePostPort deletePostPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private ImageStoragePort imageStoragePort;

    @Mock
    private be.heh.stragram.application.domain.service.PostPrivacyPolicy postPrivacyPolicy;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(loadPostPort, savePostPort, deletePostPort, loadUserPort, imageStoragePort, postPrivacyPolicy);
    }

    @Test
    void create_stores_image_and_saves_post_when_image_present() throws Exception {
        User author = MotherObjects.user().build();
        when(loadUserPort.findById(author.getId())).thenReturn(Optional.of(author));
        when(imageStoragePort.store(any(), anyString(), anyString())).thenReturn("stored/path.jpg");

        Post saved = MotherObjects.post().withAuthorId(author.getId().getValue()).withImagePath("stored/path.jpg").build();
        when(savePostPort.save(any(Post.class))).thenReturn(saved);

        CreatePostUseCase.CreatePostCommand command = CreatePostUseCase.CreatePostCommand.builder()
                .authorId(author.getId())
                .imageFile(new ByteArrayInputStream(new byte[]{1,2,3}))
                .originalFilename("img.png")
                .contentType("image/png")
                .description("desc")
                .build();

        Post result = postService.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getImagePath()).isEqualTo("stored/path.jpg");

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(savePostPort).save(captor.capture());
        assertThat(captor.getValue().getAuthorId()).isEqualTo(author.getId());
        verify(imageStoragePort).store(any(), eq("img.png"), eq("image/png"));
    }

    @Test
    void getById_throws_if_cannot_view() {
        Post post = MotherObjects.post().build();
        when(loadPostPort.findById(post.getId())).thenReturn(Optional.of(post));
        when(postPrivacyPolicy.canViewPost(eq(post), any(UserId.class))).thenReturn(false);

        assertThatThrownBy(() -> postService.getById(post.getId(), UserId.of(java.util.UUID.randomUUID())))
                .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void delete_deletes_image_and_post_when_authorized() {
        Post post = MotherObjects.post().withImagePath("img.jpg").build();
        User requester = MotherObjects.user().build();

        when(loadPostPort.findById(post.getId())).thenReturn(Optional.of(post));
        when(loadUserPort.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(postPrivacyPolicy.canDeletePost(eq(post), any(User.class))).thenReturn(true);

        postService.delete(post.getId(), requester.getId());

        verify(imageStoragePort).delete("img.jpg");
        verify(deletePostPort).delete(post);
    }

    @Test
    void listByUserId_throws_if_user_not_found() {
        UserId userId = UserId.of(java.util.UUID.randomUUID());
        when(loadUserPort.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.listByUserId(userId, 0, 10))
                .isInstanceOf(NotFoundException.class);
    }
}
