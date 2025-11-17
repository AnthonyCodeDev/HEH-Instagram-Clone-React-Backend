package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataFollowRepository;
import be.heh.stragram.adapter.out.persistence.jpa.entity.FollowRelationshipJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.FollowJpaMapper;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.FollowPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FollowRepositoryAdapter implements FollowPort {

    private final SpringDataFollowRepository followRepository;
    private final FollowJpaMapper followMapper;

    @Override
    public FollowRelationship save(FollowRelationship followRelationship) {
        FollowRelationshipJpaEntity savedEntity = followRepository.save(followMapper.toEntity(followRelationship));
        return followMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(UserId followerId, UserId followingId) {
        followRepository.deleteById_FollowerIdAndId_FollowingId(followerId.getValue(), followingId.getValue());
    }

    @Override
    public boolean exists(UserId followerId, UserId followingId) {
        return followRepository.existsById_FollowerIdAndId_FollowingId(followerId.getValue(), followingId.getValue());
    }

    @Override
    public Optional<FollowRelationship> findByFollowerIdAndFollowingId(UserId followerId, UserId followingId) {
        return followRepository.findById_FollowerIdAndId_FollowingId(followerId.getValue(), followingId.getValue())
                .map(followMapper::toDomain);
    }

    @Override
    public List<UserId> findFollowingIds(UserId followerId, int page, int size) {
        return followRepository.findFollowingIds(followerId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(UserId::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserId> findFollowerIds(UserId followingId, int page, int size) {
        return followRepository.findFollowerIds(followingId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(UserId::of)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countFollowing(UserId followerId) {
        return followRepository.countByFollowerId(followerId.getValue());
    }
    
    @Override
    public long countFollowers(UserId followingId) {
        return followRepository.countByFollowingId(followingId.getValue());
    }
}
