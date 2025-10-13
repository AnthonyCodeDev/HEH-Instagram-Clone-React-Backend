package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface AddCommentUseCase {
    
    Comment add(PostId postId, UserId authorId, AddCommentCommand command);
    
    @Value
    @Builder
    class AddCommentCommand {
        String text;
    }
}
