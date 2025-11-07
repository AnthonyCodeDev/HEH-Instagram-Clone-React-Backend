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
public class ConversationDto {
    private UUID id;
    private UUID otherUserId;
    private String otherUserUsername;
    private String otherUserAvatarUrl;
    private MessageDto lastMessage;
    private int unreadCount;
    private Instant createdAt;
    private Instant updatedAt;
}
