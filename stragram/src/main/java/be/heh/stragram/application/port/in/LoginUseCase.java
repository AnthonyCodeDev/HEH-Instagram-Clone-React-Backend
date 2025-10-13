package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface LoginUseCase {
    
    LoginResult login(LoginCommand command);
    
    @Value
    @Builder
    class LoginCommand {
        String usernameOrEmail;
        String password;
    }
    
    @Value
    @Builder
    class LoginResult {
        String token;
        UserId userId;
    }
}
