package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataLikeRepository;
import be.heh.stragram.adapter.out.persistence.jpa.entity.LikeJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.LikeJpaMapper;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LikePostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeRepositoryAdapter implements LikePostPort {

    private final SpringDataLikeRepository likeRepository;
    private final LikeJpaMapper likeMapper;

    @Override
    public Like save(Like like) {
        LikeJpaEntity savedEntity = likeRepository.save(likeMapper.toEntity(like));
        return likeMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(PostId postId, UserId userId) {
        likeRepository.deleteById_PostIdAndId_UserId(postId.getValue(), userId.getValue());
    }

    @Override
    public boolean exists(PostId postId, UserId userId) {
        return likeRepository.existsById_PostIdAndId_UserId(postId.getValue(), userId.getValue());
    }

    @Override
    public Optional<Like> findByPostIdAndUserId(PostId postId, UserId userId) {
        return likeRepository.findById_PostIdAndId_UserId(postId.getValue(), userId.getValue())
                .map(likeMapper::toDomain);
    }
    
    @Override
    public int countByPostId(PostId postId) {
        return likeRepository.countById_PostId(postId.getValue());
    }
}
