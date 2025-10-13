package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;
import lombok.Builder;
import lombok.Value;

public interface RegisterUseCase {
    
    User register(RegisterCommand command);
    
    @Value
    @Builder
    class RegisterCommand {
        String username;
        String email;
        String password;
    }
}
