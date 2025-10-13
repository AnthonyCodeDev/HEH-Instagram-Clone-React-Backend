package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.UserId;

public interface DeleteUserCommand {
    
    void delete(UserId userId, UserId requesterId);
}
