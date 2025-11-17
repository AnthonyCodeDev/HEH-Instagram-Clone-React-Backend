package be.heh.stragram.adapter.out.persistence.jpa.repository;

import be.heh.stragram.adapter.out.persistence.jpa.entity.BookmarkJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface BookmarkJpaRepository extends JpaRepository<BookmarkJpaEntity, UUID> {
    
    Optional<BookmarkJpaEntity> findByUserIdAndPostId(UUID userId, UUID postId);
    
    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
    
    void deleteByUserIdAndPostId(UUID userId, UUID postId);
    
    @Query(value = "SELECT p.* FROM posts p " +
           "INNER JOIN bookmarks b ON p.id = b.post_id " +
           "WHERE b.user_id = :userId " +
           "ORDER BY b.created_at DESC " +
           "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<be.heh.stragram.adapter.out.persistence.jpa.entity.PostJpaEntity> findBookmarkedPostsByUserId(
        @Param("userId") UUID userId, 
        @Param("offset") int offset, 
        @Param("size") int size
    );
    
    long countByUserId(UUID userId);
}
