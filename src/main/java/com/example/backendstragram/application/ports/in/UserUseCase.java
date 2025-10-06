package com.example.backendstragram.application.ports.in;

import com.example.backendstragram.domain.model.User;
import java.util.Optional;

public interface UserUseCase {
    User register(User user);
    Optional<User> findByEmail(String email);
    User login(String email, String password);
}
