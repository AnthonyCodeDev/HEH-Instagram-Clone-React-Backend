package be.heh.stragram.adapter.out.persistence;

import be.heh.stragram.adapter.out.persistence.jpa.entity.BookmarkJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.entity.PostJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.BookmarkJpaMapper;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.PostJpaMapper;
import be.heh.stragram.adapter.out.persistence.jpa.repository.BookmarkJpaRepository;
import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.BookmarkPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookmarkRepositoryAdapter implements BookmarkPort {

    private final BookmarkJpaRepository bookmarkJpaRepository;
    private final BookmarkJpaMapper bookmarkJpaMapper;
    private final PostJpaMapper postJpaMapper;

    @Override
    @Transactional
    public Bookmark save(Bookmark bookmark) {
        BookmarkJpaEntity entity = bookmarkJpaMapper.toEntity(bookmark);
        BookmarkJpaEntity saved = bookmarkJpaRepository.save(entity);
        return bookmarkJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(UserId userId, PostId postId) {
        bookmarkJpaRepository.deleteByUserIdAndPostId(
                userId.getValue(),
                postId.getValue()
        );
    }

    @Override
    public Optional<Bookmark> findByUserIdAndPostId(UserId userId, PostId postId) {
        return bookmarkJpaRepository.findByUserIdAndPostId(
                userId.getValue(),
                postId.getValue()
        ).map(bookmarkJpaMapper::toDomain);
    }

    @Override
    public List<Post> findBookmarkedPostsByUserId(UserId userId, int page, int size) {
        int offset = page * size;
        List<PostJpaEntity> entities = bookmarkJpaRepository.findBookmarkedPostsByUserId(
                userId.getValue(),
                offset,
                size
        );
        return entities.stream()
                .map(postJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserIdAndPostId(UserId userId, PostId postId) {
        return bookmarkJpaRepository.existsByUserIdAndPostId(
                userId.getValue(),
                postId.getValue()
        );
    }

    @Override
    public long countByUserId(UserId userId) {
        return bookmarkJpaRepository.countByUserId(userId.getValue());
    }
}
