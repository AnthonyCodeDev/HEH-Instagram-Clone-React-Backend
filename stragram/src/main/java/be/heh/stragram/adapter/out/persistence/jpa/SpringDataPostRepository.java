package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.PostJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID> {
    
    Page<PostJpaEntity> findByAuthorIdOrderByCreatedAtDesc(UUID authorId, Pageable pageable);
    
    @Query("SELECT p FROM PostJpaEntity p WHERE p.authorId IN " +
            "(SELECT f.id.followingId FROM FollowRelationshipJpaEntity f WHERE f.id.followerId = :userId) " +
            "ORDER BY p.createdAt DESC")
    Page<PostJpaEntity> findFeedPostsForUser(UUID userId, Pageable pageable);
}
