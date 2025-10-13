package be.heh.stragram.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class NotificationDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationResponse {
        private String id;
        private String type;
        private Map<String, String> payload;
        private boolean read;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListResponse {
        private List<NotificationResponse> notifications;
        private long unreadCount;
        private int page;
        private int size;
        private boolean hasMore;
    }
}
