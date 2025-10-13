package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.FavoriteJpaEntity;
import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class FavoriteJpaMapper {

    public Favorite toDomain(FavoriteJpaEntity entity) {
        return Favorite.reconstitute(
                PostId.of(entity.getId().getPostId()),
                UserId.of(entity.getId().getUserId()),
                entity.getCreatedAt()
        );
    }

    public FavoriteJpaEntity toEntity(Favorite domain) {
        return FavoriteJpaEntity.builder()
                .id(new FavoriteJpaEntity.FavoriteId(
                        domain.getPostId().getValue(),
                        domain.getUserId().getValue()
                ))
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
