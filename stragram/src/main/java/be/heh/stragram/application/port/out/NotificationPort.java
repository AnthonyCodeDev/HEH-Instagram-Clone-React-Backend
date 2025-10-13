package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPort {
    
    Notification save(Notification notification);
    
    Optional<Notification> findById(UUID id);
    
    List<Notification> findByUserId(UserId userId, int page, int size);
    
    long countUnreadByUserId(UserId userId);
    
    void markAllAsRead(UserId userId);
}
