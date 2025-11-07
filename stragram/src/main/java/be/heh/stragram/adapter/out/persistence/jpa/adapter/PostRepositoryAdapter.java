package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataPostRepository;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.PostJpaMapper;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.DeletePostPort;
import be.heh.stragram.application.port.out.LoadPostPort;
import be.heh.stragram.application.port.out.SavePostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter implements LoadPostPort, SavePostPort, DeletePostPort {

    private final SpringDataPostRepository postRepository;
    private final PostJpaMapper postMapper;

    @Override
    public Optional<Post> findById(PostId id) {
        return postRepository.findById(id.getValue())
                .map(postMapper::toDomain);
    }

    @Override
    public List<Post> findByAuthorId(UserId authorId, int page, int size) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByUserFeed(UserId userId, int page, int size) {
        return postRepository.findFeedPostsForUser(userId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findRecentPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Post save(Post post) {
        return postMapper.toDomain(postRepository.save(postMapper.toEntity(post)));
    }

    @Override
    public void delete(Post post) {
        postRepository.deleteById(post.getId().getValue());
    }
}
