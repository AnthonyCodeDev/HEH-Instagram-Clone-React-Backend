package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface GetNotificationsQuery {
    
    List<Notification> list(UserId userId, int page, int size);
    
    long countUnread(UserId userId);
}
