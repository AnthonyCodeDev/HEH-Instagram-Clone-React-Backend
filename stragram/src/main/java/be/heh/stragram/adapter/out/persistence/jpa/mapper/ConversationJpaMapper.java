package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.ConversationJpaEntity;
import be.heh.stragram.application.domain.model.Conversation;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConversationJpaMapper {

    public ConversationJpaEntity toJpaEntity(Conversation conversation) {
        return ConversationJpaEntity.builder()
                .id(conversation.getId().getValue())
                .participantIds(conversation.getParticipantIds().stream()
                        .map(UserId::getValue)
                        .collect(Collectors.toSet()))
                .deletedByUserIds(conversation.getDeletedByUserIds().stream()
                        .map(UserId::getValue)
                        .collect(Collectors.toSet()))
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .lastMessageId(conversation.getLastMessageId() != null 
                        ? conversation.getLastMessageId().getValue() 
                        : null)
                .build();
    }

    public Conversation toDomainModel(ConversationJpaEntity entity) {
        Set<UserId> participantIds = entity.getParticipantIds().stream()
                .map(UserId::of)
                .collect(Collectors.toSet());

        Set<UserId> deletedByUserIds = entity.getDeletedByUserIds() != null
                ? entity.getDeletedByUserIds().stream()
                        .map(UserId::of)
                        .collect(Collectors.toSet())
                : new java.util.HashSet<>();

        return Conversation.reconstitute(
                ConversationId.of(entity.getId()),
                participantIds,
                deletedByUserIds,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastMessageId() != null 
                        ? MessageId.of(entity.getLastMessageId()) 
                        : null
        );
    }
}
