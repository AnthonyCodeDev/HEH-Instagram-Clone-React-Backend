package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.CommentDtos;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.port.out.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentWebMapper {

    private final LoadUserPort loadUserPort;

    public CommentDtos.CommentResponse toCommentResponse(Comment comment) {
        User author = loadUserPort.findById(comment.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Comment author not found"));

        return CommentDtos.CommentResponse.builder()
                .id(comment.getId().toString())
                .postId(comment.getPostId().toString())
                .authorId(author.getId().toString())
                .authorUsername(author.getUsername().toString())
                .authorAvatarUrl(author.getAvatarUrl())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
