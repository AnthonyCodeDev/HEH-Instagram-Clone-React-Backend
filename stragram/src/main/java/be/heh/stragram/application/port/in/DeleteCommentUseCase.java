package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.UserId;

public interface DeleteCommentUseCase {
    
    void delete(CommentId commentId, UserId requesterId);
}
