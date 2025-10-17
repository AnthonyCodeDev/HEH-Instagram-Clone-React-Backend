package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.AuthDtos;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        log.info("Received register request for username: {}", request.getUsername());
        
        // Validation supplémentaire si nécessaire
        validateRegistrationRequest(request);
        
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
        
        log.info("Registration successful for username: {}", request.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        log.info("Received login request for: {}", request.getUsernameOrEmail());
        
        LoginUseCase.LoginResult loginResult = loginUseCase.login(LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(request.getUsernameOrEmail())
                .password(request.getPassword())
                .build());

        AuthDtos.AuthResponse response = AuthDtos.AuthResponse.builder()
                .token(loginResult.getToken())
                .userId(loginResult.getUserId().toString())
                .username(request.getUsernameOrEmail()) // This is simplified; in a real app, you'd fetch the user
                .build();
        
        log.info("Login successful for: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }
    
    // Simple endpoint pour tester l'accès
    @GetMapping("/test")
    public ResponseEntity<String> testAuth() {
        log.info("Auth test endpoint accessed");
        return ResponseEntity.ok("Auth endpoints are accessible");
    }
    
    /**
     * Validation supplémentaire pour les requêtes d'inscription
     * @param request La requête d'inscription
     * @throws ValidationException Si la validation échoue
     */
    private void validateRegistrationRequest(AuthDtos.RegisterRequest request) {
        // Ces validations sont complémentaires à celles déjà définies par les annotations @Valid
        if (request.getUsername().length() > 30) {
            throw new ValidationException("Le nom d'utilisateur ne doit pas dépasser 30 caractères");
        }
        
        if (request.getEmail().length() > 100) {
            throw new ValidationException("L'email ne doit pas dépasser 100 caractères");
        }
        
        // Validation du format de l'email (en plus de l'annotation @Email)
        if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
            throw new ValidationException("Format d'email invalide");
        }
    }
}