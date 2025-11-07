package be.heh.stragram.adapter.in.websocket;

import be.heh.stragram.adapter.in.web.dto.MessageDto;
import be.heh.stragram.adapter.in.web.dto.SendMessageRequest;
import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;
    private final LoadUserPort loadUserPort;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        try {
            UserId senderId = UserId.of(UUID.fromString(principal.getName()));
            UserId receiverId = UserId.of(request.getReceiverId());

            log.info("WebSocket message from {} to {}", senderId, receiverId);

            // Envoyer le message
            Message message = messageService.sendMessage(senderId, receiverId, request.getContent());

            // Charger les informations de l'expéditeur
            User sender = loadUserPort.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

            // Créer le DTO
            MessageDto messageDto = MessageDto.builder()
                    .id(message.getId().getValue())
                    .conversationId(message.getConversationId().getValue())
                    .senderId(message.getSenderId().getValue())
                    .senderUsername(sender.getUsername().toString())
                    .senderAvatarUrl(sender.getAvatarUrl())
                    .content(message.getContent())
                    .sentAt(message.getSentAt())
                    .isRead(message.isRead())
                    .readAt(message.getReadAt())
                    .build();

            // Envoyer au destinataire via WebSocket
            messagingTemplate.convertAndSendToUser(
                    receiverId.getValue().toString(),
                    "/queue/messages",
                    messageDto
            );

            // Envoyer aussi à l'expéditeur pour confirmation
            messagingTemplate.convertAndSendToUser(
                    senderId.getValue().toString(),
                    "/queue/messages",
                    messageDto
            );

            log.info("Message sent successfully: {}", message.getId());
        } catch (Exception e) {
            log.error("Error sending message via WebSocket", e);
            throw e;
        }
    }

    @MessageMapping("/chat.typing")
    public void userTyping(@Payload UUID receiverId, Principal principal) {
        try {
            UserId senderId = UserId.of(UUID.fromString(principal.getName()));
            
            // Notifier le destinataire que l'utilisateur est en train d'écrire
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/typing",
                    senderId.getValue()
            );
        } catch (Exception e) {
            log.error("Error handling typing event", e);
        }
    }
}
