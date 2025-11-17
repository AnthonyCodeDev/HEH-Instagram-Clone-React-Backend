package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.LikeJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.entity.PostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLikeRepository extends JpaRepository<LikeJpaEntity, LikeJpaEntity.LikeId> {
    
    Optional<LikeJpaEntity> findById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    boolean existsById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    void deleteById_PostIdAndId_UserId(UUID postId, UUID userId);
    
    int countById_PostId(UUID postId);
    
    @Query(value = "SELECT p.* FROM posts p " +
           "INNER JOIN likes l ON p.id = l.post_id " +
           "WHERE l.user_id = :userId " +
           "ORDER BY l.created_at DESC " +
           "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<PostJpaEntity> findLikedPostsByUserId(
        @Param("userId") UUID userId, 
        @Param("offset") int offset, 
        @Param("size") int size
    );
    
    long countById_UserId(UUID userId);
}
