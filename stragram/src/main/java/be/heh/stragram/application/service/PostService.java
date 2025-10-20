package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.service.PostPrivacyPolicy;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.*;
import be.heh.stragram.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements CreatePostUseCase, GetPostQuery, ListUserPostsQuery, 
        UpdatePostUseCase, DeletePostUseCase, ListRecentPostsQuery {

    private final LoadPostPort loadPostPort;
    private final SavePostPort savePostPort;
    private final DeletePostPort deletePostPort;
    private final LoadUserPort loadUserPort;
    private final ImageStoragePort imageStoragePort;
    private final PostPrivacyPolicy postPrivacyPolicy;

    @Override
    @Transactional
    public Post create(CreatePostCommand command) {
        // Verify user exists
        User author = loadUserPort.findById(command.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User", command.getAuthorId().toString()));

        // Store image si présente
        String imagePath = null;
        if (command.getImageFile() != null) {
            imagePath = imageStoragePort.store(
                    command.getImageFile(),
                    command.getOriginalFilename(),
                    command.getContentType()
            );
        }

        // Create post
        Post post = Post.create(
                command.getAuthorId(),
                imagePath,
                command.getDescription()
        );

        return savePostPort.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getById(PostId postId, UserId requesterId) {
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Check if user can view post
        if (!postPrivacyPolicy.canViewPost(post, requesterId)) {
            throw new UnauthorizedActionException("You don't have permission to view this post");
        }

        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> listByUserId(UserId userId, int page, int size) {
        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        return loadPostPort.findByAuthorId(userId, page, size);
    }

    @Override
    @Transactional
    public Post update(PostId postId, UserId requesterId, UpdatePostCommand command) {
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Check if user can edit post
        if (!postPrivacyPolicy.canEditPost(post, requesterId)) {
            throw new UnauthorizedActionException("You don't have permission to edit this post");
        }

        post.updateDescription(command.getDescription(), requesterId);
        
        return savePostPort.save(post);
    }

    @Override
    @Transactional
    public void delete(PostId postId, UserId requesterId) {
        Post post = loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        User requester = loadUserPort.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User", requesterId.toString()));

        // Check if user can delete post
        if (!postPrivacyPolicy.canDeletePost(post, requester)) {
            throw new UnauthorizedActionException("You don't have permission to delete this post");
        }

        // Delete image if exists
        if (post.getImagePath() != null) {
            imageStoragePort.delete(post.getImagePath());
        }
        
        // Delete post
        deletePostPort.delete(post);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Post> listRecentPosts(int page, int size, UserId currentUserId) {
        // Récupérer les posts récents
        return loadPostPort.findRecentPosts(page, size);
    }
}
