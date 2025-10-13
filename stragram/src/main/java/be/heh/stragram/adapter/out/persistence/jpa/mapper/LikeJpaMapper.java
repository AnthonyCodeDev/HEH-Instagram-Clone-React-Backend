package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.LikeJpaEntity;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class LikeJpaMapper {

    public Like toDomain(LikeJpaEntity entity) {
        return Like.reconstitute(
                PostId.of(entity.getId().getPostId()),
                UserId.of(entity.getId().getUserId()),
                entity.getCreatedAt()
        );
    }

    public LikeJpaEntity toEntity(Like domain) {
        return LikeJpaEntity.builder()
                .id(new LikeJpaEntity.LikeId(
                        domain.getPostId().getValue(),
                        domain.getUserId().getValue()
                ))
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
