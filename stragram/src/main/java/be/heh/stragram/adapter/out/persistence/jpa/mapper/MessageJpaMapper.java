package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.MessageJpaEntity;
import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class MessageJpaMapper {

    public MessageJpaEntity toJpaEntity(Message message) {
        return MessageJpaEntity.builder()
                .id(message.getId().getValue())
                .conversationId(message.getConversationId().getValue())
                .senderId(message.getSenderId().getValue())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .build();
    }

    public Message toDomainModel(MessageJpaEntity entity) {
        return Message.reconstitute(
                MessageId.of(entity.getId()),
                ConversationId.of(entity.getConversationId()),
                UserId.of(entity.getSenderId()),
                entity.getContent(),
                entity.getSentAt(),
                entity.isRead(),
                entity.getReadAt()
        );
    }
}
