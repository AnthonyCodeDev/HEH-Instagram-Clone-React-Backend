package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.FavoriteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFavoriteRepository extends JpaRepository<FavoriteJpaEntity, FavoriteJpaEntity.FavoriteId> {
    
    Optional<FavoriteJpaEntity> findById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    boolean existsById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    void deleteById_PostIdAndId_UserId(UUID postId, UUID userId);
}
