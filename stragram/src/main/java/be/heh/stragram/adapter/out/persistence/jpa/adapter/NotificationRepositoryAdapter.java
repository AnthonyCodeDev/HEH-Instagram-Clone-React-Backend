package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataNotificationRepository;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.NotificationJpaMapper;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationPort {

    private final SpringDataNotificationRepository notificationRepository;
    private final NotificationJpaMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        return notificationMapper.toDomain(notificationRepository.save(notificationMapper.toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UserId userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadByUserId(UserId userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId.getValue());
    }

    @Override
    @Transactional
    public void markAllAsRead(UserId userId) {
        notificationRepository.markAllAsRead(userId.getValue());
    }
}
