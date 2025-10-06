package com.example.backendstragram.adapter.in.rest;

import com.example.backendstragram.adapter.in.dto.*;
import com.example.backendstragram.config.JwtService;
import com.example.backendstragram.domain.model.User;
import com.example.backendstragram.application.ports.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequiredArgsConstructor
public class UserController {
    private final UserUseCase userUseCase;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserRegistrationRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        User savedUser = userUseCase.register(user);
        String token = jwtService.generateToken(savedUser.getEmail());

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        User user = userUseCase.login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        return userUseCase.findByEmail(email)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        return userUseCase.findByEmail(email)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
