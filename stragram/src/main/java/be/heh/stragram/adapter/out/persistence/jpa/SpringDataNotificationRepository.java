package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.NotificationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, UUID> {
    
    Page<NotificationJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    long countByUserIdAndIsReadFalse(UUID userId);
    
    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsRead(UUID userId);
}
