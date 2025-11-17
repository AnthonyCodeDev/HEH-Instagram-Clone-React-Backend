package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.BookmarkJpaEntity;
import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.value.BookmarkId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class BookmarkJpaMapper {

    public Bookmark toDomain(BookmarkJpaEntity entity) {
        return Bookmark.reconstitute(
                BookmarkId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                PostId.of(entity.getPostId()),
                entity.getCreatedAt()
        );
    }

    public BookmarkJpaEntity toEntity(Bookmark bookmark) {
        return BookmarkJpaEntity.builder()
                .id(bookmark.getId().getValue())
                .userId(bookmark.getUserId().getValue())
                .postId(bookmark.getPostId().getValue())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
