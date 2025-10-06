package com.example.backendstragram.application.ports.out;

import com.example.backendstragram.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
}
