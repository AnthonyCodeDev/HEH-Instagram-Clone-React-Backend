package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.NotificationDtos;
import be.heh.stragram.adapter.in.web.mapper.NotificationWebMapper;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.GetNotificationsQuery;
import be.heh.stragram.application.port.in.MarkNotificationReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetNotificationsQuery getNotificationsQuery;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final NotificationWebMapper notificationWebMapper;

    @GetMapping
    public ResponseEntity<NotificationDtos.NotificationListResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        List<Notification> notifications = getNotificationsQuery.list(currentUserId, page, size);
        long unreadCount = getNotificationsQuery.countUnread(currentUserId);
        
        List<NotificationDtos.NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationWebMapper::toNotificationResponse)
                .collect(Collectors.toList());

        NotificationDtos.NotificationListResponse response = NotificationDtos.NotificationListResponse.builder()
                .notifications(notificationResponses)
                .unreadCount(unreadCount)
                .page(page)
                .size(size)
                .hasMore(notificationResponses.size() == size)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationRead(
            @PathVariable String id,
            @AuthenticationPrincipal UserId currentUserId) {
        
        markNotificationReadUseCase.markRead(UUID.fromString(id), currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsRead(
            @AuthenticationPrincipal UserId currentUserId) {
        
        markNotificationReadUseCase.markAllRead(currentUserId);
        return ResponseEntity.noContent().build();
    }
}
