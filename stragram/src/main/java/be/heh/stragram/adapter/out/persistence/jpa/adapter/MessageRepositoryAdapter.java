package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataMessageRepository;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.MessageJpaMapper;
import be.heh.stragram.application.domain.model.Message;
import be.heh.stragram.application.domain.value.ConversationId;
import be.heh.stragram.application.domain.value.MessageId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LoadMessagesPort;
import be.heh.stragram.application.port.out.SaveMessagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements LoadMessagesPort, SaveMessagePort {

    private final SpringDataMessageRepository repository;
    private final MessageJpaMapper mapper;

    @Override
    public Optional<Message> findById(MessageId id) {
        return repository.findById(id.getValue())
                .map(mapper::toDomainModel);
    }

    @Override
    public List<Message> findByConversationId(ConversationId conversationId) {
        return repository.findByConversationIdOrderBySentAtAsc(conversationId.getValue())
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findUnreadByConversationIdAndReceiverId(
            ConversationId conversationId, 
            UserId receiverId
    ) {
        return repository.findUnreadByConversationIdAndReceiverId(
                        conversationId.getValue(),
                        receiverId.getValue()
                )
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public int countUnreadByReceiverId(UserId receiverId) {
        return repository.countUnreadByReceiverId(receiverId.getValue());
    }

    @Override
    public Message save(Message message) {
        var entity = mapper.toJpaEntity(message);
        var saved = repository.save(entity);
        return mapper.toDomainModel(saved);
    }
}
