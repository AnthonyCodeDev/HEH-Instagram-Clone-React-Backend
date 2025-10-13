package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;

import java.util.Optional;

public interface LoadUserPort {
    
    Optional<User> findById(UserId id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
