package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.value.ConversationId;

public interface DeleteConversationPort {
    void delete(ConversationId conversationId);
}
