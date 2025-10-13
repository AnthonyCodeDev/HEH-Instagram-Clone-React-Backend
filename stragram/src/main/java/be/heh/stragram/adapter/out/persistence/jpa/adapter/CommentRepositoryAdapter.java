package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataCommentRepository;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.CommentJpaMapper;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.port.out.DeleteCommentPort;
import be.heh.stragram.application.port.out.LoadCommentsPort;
import be.heh.stragram.application.port.out.SaveCommentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements LoadCommentsPort, SaveCommentPort, DeleteCommentPort {

    private final SpringDataCommentRepository commentRepository;
    private final CommentJpaMapper commentMapper;

    @Override
    public Optional<Comment> findById(CommentId id) {
        return commentRepository.findById(id.getValue())
                .map(commentMapper::toDomain);
    }

    @Override
    public List<Comment> findByPostId(PostId postId, int page, int size) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId.getValue(), PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Comment save(Comment comment) {
        return commentMapper.toDomain(commentRepository.save(commentMapper.toEntity(comment)));
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.deleteById(comment.getId().getValue());
    }
}
