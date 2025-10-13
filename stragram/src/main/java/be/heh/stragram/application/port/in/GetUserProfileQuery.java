package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;

public interface GetUserProfileQuery {
    
    User getUserById(UserId userId);
    
    User getUserByUsername(String username);
}
