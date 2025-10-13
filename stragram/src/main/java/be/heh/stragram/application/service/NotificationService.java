package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.GetNotificationsQuery;
import be.heh.stragram.application.port.in.MarkNotificationReadUseCase;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService implements GetNotificationsQuery, MarkNotificationReadUseCase {

    private final NotificationPort notificationPort;
    private final LoadUserPort loadUserPort;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> list(UserId userId, int page, int size) {
        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        return notificationPort.findByUserId(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UserId userId) {
        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        return notificationPort.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markRead(UUID notificationId, UserId userId) {
        Notification notification = notificationPort.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification", notificationId.toString()));

        // Check if notification belongs to user
        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You don't have permission to update this notification");
        }

        notification.markAsRead();
        notificationPort.save(notification);
    }

    @Override
    @Transactional
    public void markAllRead(UserId userId) {
        // Verify user exists
        loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        notificationPort.markAllAsRead(userId);
    }
}
