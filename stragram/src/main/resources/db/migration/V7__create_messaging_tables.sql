    -- Migration V5: Create tables for messaging system
-- Creates conversations, conversation_participants, and messages tables

CREATE TABLE conversations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    last_message_id UUID
);

CREATE TABLE conversation_participants (
    conversation_id UUID NOT NULL,
    participant_id UUID NOT NULL,
    PRIMARY KEY (conversation_id, participant_id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    content VARCHAR(1000) NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances des requêtes fréquentes
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
CREATE INDEX idx_messages_is_read ON messages(is_read);
CREATE INDEX idx_conversation_participants_participant_id ON conversation_participants(participant_id);
CREATE INDEX idx_conversations_updated_at ON conversations(updated_at);

-- Contrainte pour s'assurer qu'une conversation a exactement 2 participants
-- Note: Cette contrainte sera appliquée au niveau de l'application
