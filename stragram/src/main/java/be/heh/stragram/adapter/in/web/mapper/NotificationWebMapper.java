package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.NotificationDtos;
import be.heh.stragram.application.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationWebMapper {

    public NotificationDtos.NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationDtos.NotificationResponse.builder()
                .id(notification.getId().toString())
                .type(notification.getType().name())
                .payload(notification.getPayload())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
