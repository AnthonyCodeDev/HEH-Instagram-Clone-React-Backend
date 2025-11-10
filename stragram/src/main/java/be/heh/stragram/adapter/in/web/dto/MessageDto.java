package be.heh.stragram.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private String content;
    private Instant sentAt;
    private boolean isRead;
    private Instant readAt;
}
