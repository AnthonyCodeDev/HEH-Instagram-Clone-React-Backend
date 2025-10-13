package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface UpdateUserProfileCommand {
    
    User update(UserId userId, UpdateProfileCommand command);
    
    @Value
    @Builder
    class UpdateProfileCommand {
        String username;
        String email;
        String bio;
        String avatarUrl;
    }
}
