package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.FollowRelationshipJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFollowRepository extends JpaRepository<FollowRelationshipJpaEntity, FollowRelationshipJpaEntity.FollowId> {
    
    Optional<FollowRelationshipJpaEntity> findById_FollowerIdAndId_FollowingId(UUID followerId, UUID followingId);
    
    boolean existsById_FollowerIdAndId_FollowingId(UUID followerId, UUID followingId);
    
    void deleteById_FollowerIdAndId_FollowingId(UUID followerId, UUID followingId);
    
    @Query("SELECT f.id.followingId FROM FollowRelationshipJpaEntity f WHERE f.id.followerId = :followerId")
    Page<UUID> findFollowingIds(UUID followerId, Pageable pageable);
    
    @Query("SELECT f.id.followerId FROM FollowRelationshipJpaEntity f WHERE f.id.followingId = :followingId")
    Page<UUID> findFollowerIds(UUID followingId, Pageable pageable);
    
    @Query("SELECT COUNT(f) FROM FollowRelationshipJpaEntity f WHERE f.id.followerId = :followerId")
    long countByFollowerId(UUID followerId);
    
    @Query("SELECT COUNT(f) FROM FollowRelationshipJpaEntity f WHERE f.id.followingId = :followingId")
    long countByFollowingId(UUID followingId);
}
