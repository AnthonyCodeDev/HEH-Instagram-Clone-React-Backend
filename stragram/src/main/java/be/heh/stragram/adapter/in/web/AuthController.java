package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.AuthDtos;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        User user = registerUseCase.register(RegisterUseCase.RegisterCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build());

        LoginUseCase.LoginResult loginResult = loginUseCase.login(LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(request.getUsername())
                .password(request.getPassword())
                .build());

        AuthDtos.AuthResponse response = AuthDtos.AuthResponse.builder()
                .token(loginResult.getToken())
                .userId(user.getId().toString())
                .username(user.getUsername().toString())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        LoginUseCase.LoginResult loginResult = loginUseCase.login(LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(request.getUsernameOrEmail())
                .password(request.getPassword())
                .build());

        AuthDtos.AuthResponse response = AuthDtos.AuthResponse.builder()
                .token(loginResult.getToken())
                .userId(loginResult.getUserId().toString())
                .username(request.getUsernameOrEmail()) // This is simplified; in a real app, you'd fetch the user
                .build();

        return ResponseEntity.ok(response);
    }
}
