package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Conversation;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface LoadConversationsPort {
    Optional<Conversation> findById(ConversationId id);
    Optional<Conversation> findByParticipants(UserId user1Id, UserId user2Id);
    List<Conversation> findByParticipantId(UserId userId);
}
