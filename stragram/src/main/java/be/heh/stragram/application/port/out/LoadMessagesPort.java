package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface LoadMessagesPort {
    Optional<Message> findById(MessageId id);
    List<Message> findByConversationId(ConversationId conversationId);
    List<Message> findUnreadByConversationIdAndReceiverId(ConversationId conversationId, UserId receiverId);
    int countUnreadByReceiverId(UserId receiverId);
}
