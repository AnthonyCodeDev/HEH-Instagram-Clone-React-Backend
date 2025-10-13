package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.NotificationJpaEntity;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;
import org.springframework.stereotype.Component;

@Component
public class NotificationJpaMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        return Notification.reconstitute(
                entity.getId(),
                UserId.of(entity.getUserId()),
                Notification.NotificationType.valueOf(entity.getType()),
                entity.getPayload(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }

    public NotificationJpaEntity toEntity(Notification domain) {
        return NotificationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId().getValue())
                .type(domain.getType().name())
                .payload(domain.getPayload())
                .isRead(domain.isRead())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
