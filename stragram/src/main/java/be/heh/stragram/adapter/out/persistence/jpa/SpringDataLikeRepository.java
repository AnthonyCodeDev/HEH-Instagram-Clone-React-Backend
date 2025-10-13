package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.LikeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLikeRepository extends JpaRepository<LikeJpaEntity, LikeJpaEntity.LikeId> {
    
    Optional<LikeJpaEntity> findById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    boolean existsById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    void deleteById_PostIdAndId_UserId(UUID postId, UUID userId);
}
