package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.UserId;

import java.util.UUID;

public interface MarkNotificationReadUseCase {
    
    void markRead(UUID notificationId, UserId userId);
    
    void markAllRead(UserId userId);
}
