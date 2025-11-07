package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.entity.ConversationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataConversationRepository extends JpaRepository<ConversationJpaEntity, UUID> {
    
    @Query("SELECT c FROM ConversationJpaEntity c WHERE :userId MEMBER OF c.participantIds " +
           "ORDER BY c.updatedAt DESC")
    List<ConversationJpaEntity> findByParticipantId(@Param("userId") UUID userId);
    
    @Query("SELECT c FROM ConversationJpaEntity c WHERE :user1Id MEMBER OF c.participantIds " +
           "AND :user2Id MEMBER OF c.participantIds")
    Optional<ConversationJpaEntity> findByParticipants(
        @Param("user1Id") UUID user1Id,
        @Param("user2Id") UUID user2Id
    );
}
