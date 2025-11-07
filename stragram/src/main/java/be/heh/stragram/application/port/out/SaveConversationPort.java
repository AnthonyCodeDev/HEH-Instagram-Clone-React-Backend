package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Conversation;

public interface SaveConversationPort {
    Conversation save(Conversation conversation);
}
