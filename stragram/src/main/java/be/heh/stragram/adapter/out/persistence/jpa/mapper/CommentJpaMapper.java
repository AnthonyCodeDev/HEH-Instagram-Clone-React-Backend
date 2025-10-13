package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.CommentJpaEntity;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class CommentJpaMapper {

    public Comment toDomain(CommentJpaEntity entity) {
        return Comment.reconstitute(
                CommentId.of(entity.getId()),
                PostId.of(entity.getPostId()),
                UserId.of(entity.getAuthorId()),
                entity.getText(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CommentJpaEntity toEntity(Comment domain) {
        return CommentJpaEntity.builder()
                .id(domain.getId().getValue())
                .postId(domain.getPostId().getValue())
                .authorId(domain.getAuthorId().getValue())
                .text(domain.getText())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
