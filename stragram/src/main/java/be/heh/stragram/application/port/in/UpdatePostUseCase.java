package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface UpdatePostUseCase {
    
    Post update(PostId postId, UserId requesterId, UpdatePostCommand command);
    
    @Value
    @Builder
    class UpdatePostCommand {
        String description;
    }
}
