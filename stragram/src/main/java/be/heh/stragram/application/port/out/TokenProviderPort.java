package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.value.UserId;

import java.util.Optional;

public interface TokenProviderPort {
    
    String generateToken(UserId userId, String username, boolean isAdmin);
    
    Optional<UserId> validateTokenAndGetUserId(String token);
    
    boolean isAdmin(String token);
}
