package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface EditCommentUseCase {
    
    Comment edit(CommentId commentId, UserId requesterId, EditCommentCommand command);
    
    @Value
    @Builder
    class EditCommentCommand {
        String text;
    }
}
