package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.CommentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataCommentRepository extends JpaRepository<CommentJpaEntity, UUID> {
    
    Page<CommentJpaEntity> findByPostIdOrderByCreatedAtAsc(UUID postId, Pageable pageable);
}
