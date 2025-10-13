package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Post;

public interface DeletePostPort {
    
    void delete(Post post);
}
