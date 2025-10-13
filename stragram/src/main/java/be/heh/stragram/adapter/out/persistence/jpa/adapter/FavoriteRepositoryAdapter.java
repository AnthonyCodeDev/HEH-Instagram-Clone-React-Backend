package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataFavoriteRepository;
import be.heh.stragram.adapter.out.persistence.jpa.entity.FavoriteJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.FavoriteJpaMapper;
import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.FavoritePostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FavoriteRepositoryAdapter implements FavoritePostPort {

    private final SpringDataFavoriteRepository favoriteRepository;
    private final FavoriteJpaMapper favoriteMapper;

    @Override
    public Favorite save(Favorite favorite) {
        FavoriteJpaEntity savedEntity = favoriteRepository.save(favoriteMapper.toEntity(favorite));
        return favoriteMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(PostId postId, UserId userId) {
        favoriteRepository.deleteById_PostIdAndId_UserId(postId.getValue(), userId.getValue());
    }

    @Override
    public boolean exists(PostId postId, UserId userId) {
        return favoriteRepository.existsById_PostIdAndId_UserId(postId.getValue(), userId.getValue());
    }

    @Override
    public Optional<Favorite> findByPostIdAndUserId(PostId postId, UserId userId) {
        return favoriteRepository.findById_PostIdAndId_UserId(postId.getValue(), userId.getValue())
                .map(favoriteMapper::toDomain);
    }
}
