package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.domain.value.Username;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.PasswordEncoderPort;
import be.heh.stragram.application.port.out.SaveUserPort;
import be.heh.stragram.application.port.out.TokenProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements RegisterUseCase, LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    @Override
    @Transactional
    public User register(RegisterCommand command) {
        // Validate username and email are not taken
        if (loadUserPort.existsByUsername(command.getUsername())) {
            throw new ValidationException("Username is already taken");
        }

        if (loadUserPort.existsByEmail(command.getEmail())) {
            throw new ValidationException("Email is already taken");
        }

        // Create user with validated values
        Username username = Username.of(command.getUsername());
        Email email = Email.of(command.getEmail());
        PasswordHash passwordHash = passwordEncoderPort.encode(command.getPassword());

        User user = User.create(username, email, passwordHash);
        
        return saveUserPort.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        User user = loadUserPort.findByUsernameOrEmail(command.getUsernameOrEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Invalid credentials"));

        if (!passwordEncoderPort.matches(command.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedActionException("Invalid credentials");
        }

        String token = tokenProviderPort.generateToken(
                user.getId(),
                user.getUsername().toString(),
                user.isAdmin()
        );

        return LoginResult.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }
}
