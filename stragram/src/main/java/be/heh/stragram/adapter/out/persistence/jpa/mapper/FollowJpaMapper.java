package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.FollowRelationshipJpaEntity;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class FollowJpaMapper {

    public FollowRelationship toDomain(FollowRelationshipJpaEntity entity) {
        return FollowRelationship.reconstitute(
                UserId.of(entity.getId().getFollowerId()),
                UserId.of(entity.getId().getFollowingId()),
                entity.getCreatedAt()
        );
    }

    public FollowRelationshipJpaEntity toEntity(FollowRelationship domain) {
        return FollowRelationshipJpaEntity.builder()
                .id(new FollowRelationshipJpaEntity.FollowId(
                        domain.getFollowerId().getValue(),
                        domain.getFollowingId().getValue()
                ))
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
