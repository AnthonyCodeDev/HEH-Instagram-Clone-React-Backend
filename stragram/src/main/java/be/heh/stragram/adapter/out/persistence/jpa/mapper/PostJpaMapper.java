package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.PostJpaEntity;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class PostJpaMapper {

    public Post toDomain(PostJpaEntity entity) {
        return Post.reconstitute(
                PostId.of(entity.getId()),
                UserId.of(entity.getAuthorId()),
                entity.getImagePath(),
                entity.getDescription(),
                entity.getLikeCount(),
                entity.getCommentCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PostJpaEntity toEntity(Post domain) {
        return PostJpaEntity.builder()
                .id(domain.getId().getValue())
                .authorId(domain.getAuthorId().getValue())
                .imagePath(domain.getImagePath())
                .description(domain.getDescription())
                .likeCount(domain.getLikeCount())
                .commentCount(domain.getCommentCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
