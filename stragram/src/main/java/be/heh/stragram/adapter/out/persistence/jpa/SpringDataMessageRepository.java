package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.MessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataMessageRepository extends JpaRepository<MessageJpaEntity, UUID> {
    
    List<MessageJpaEntity> findByConversationIdOrderBySentAtAsc(UUID conversationId);
    
    @Query("SELECT m FROM MessageJpaEntity m WHERE m.conversationId = :conversationId " +
           "AND m.senderId != :receiverId AND m.isRead = false")
    List<MessageJpaEntity> findUnreadByConversationIdAndReceiverId(
        @Param("conversationId") UUID conversationId,
        @Param("receiverId") UUID receiverId
    );
    
    @Query("SELECT COUNT(m) FROM MessageJpaEntity m " +
           "JOIN ConversationJpaEntity c ON m.conversationId = c.id " +
           "WHERE :userId MEMBER OF c.participantIds " +
           "AND m.senderId != :userId " +
           "AND m.isRead = false")
    int countUnreadByReceiverId(@Param("userId") UUID userId);
}
