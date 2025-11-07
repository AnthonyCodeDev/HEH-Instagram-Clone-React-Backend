package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataConversationRepository;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.ConversationJpaMapper;
import be.heh.stragram.application.domain.model.Conversation;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LoadConversationsPort;
import be.heh.stragram.application.port.out.SaveConversationPort;
import be.heh.stragram.application.port.out.DeleteConversationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements LoadConversationsPort, SaveConversationPort, DeleteConversationPort {

    private final SpringDataConversationRepository repository;
    private final ConversationJpaMapper mapper;

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return repository.findById(id.getValue())
                .map(mapper::toDomainModel);
    }

    @Override
    public Optional<Conversation> findByParticipants(UserId user1Id, UserId user2Id) {
        return repository.findByParticipants(user1Id.getValue(), user2Id.getValue())
                .map(mapper::toDomainModel);
    }

    @Override
    public List<Conversation> findByParticipantId(UserId userId) {
        return repository.findByParticipantId(userId.getValue())
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Conversation save(Conversation conversation) {
        var entity = mapper.toJpaEntity(conversation);
        var saved = repository.save(entity);
        return mapper.toDomainModel(saved);
    }

    @Override
    public void delete(ConversationId conversationId) {
        repository.deleteById(conversationId.getValue());
    }
}
