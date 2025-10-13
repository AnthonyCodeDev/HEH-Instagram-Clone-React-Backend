package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Comment;

public interface SaveCommentPort {
    
    Comment save(Comment comment);
}
