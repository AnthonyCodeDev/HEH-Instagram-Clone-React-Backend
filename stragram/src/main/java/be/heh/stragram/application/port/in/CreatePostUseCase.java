package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

import java.io.InputStream;

public interface CreatePostUseCase {
    
    Post create(CreatePostCommand command);
    
    @Value
    @Builder
    class CreatePostCommand {
        UserId authorId;
        InputStream imageFile;
        String originalFilename;
        String contentType;
        String description;
    }
}
