package be.heh.stragram.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "conversation_participants",
        joinColumns = @JoinColumn(name = "conversation_id")
    )
    @Column(name = "participant_id")
    private Set<UUID> participantIds;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_message_id")
    private UUID lastMessageId;
}
