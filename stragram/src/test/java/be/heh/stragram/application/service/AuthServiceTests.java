package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.Username;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import be.heh.stragram.application.port.out.ClockPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.PasswordEncoderPort;
import be.heh.stragram.application.port.out.SaveUserPort;
import be.heh.stragram.application.port.out.TokenProviderPort;
import be.heh.stragram.testutil.FixedClock;
import be.heh.stragram.testutil.MotherObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private LoadUserPort loadUserPort;
    
    @Mock
    private SaveUserPort saveUserPort;
    
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    
    @Mock
    private TokenProviderPort tokenProviderPort;
    
    private ClockPort clockPort = new FixedClock("2025-10-13T12:00:00Z");
    
    private AuthService authService;
    
    @BeforeEach
    void setUp() {
        authService = new AuthService(loadUserPort, saveUserPort, passwordEncoderPort, tokenProviderPort);
    }
    
    @Test
    void register_creates_user_and_returns_user() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "Password123";
        
        when(loadUserPort.existsByUsername(username)).thenReturn(false);
        when(loadUserPort.existsByEmail(email)).thenReturn(false);
        when(passwordEncoderPort.encode(password)).thenReturn(PasswordHash.of("hashed_password"));
        
        User savedUser = MotherObjects.user()
                .withUsername(username)
                .withEmail(email)
                .withPasswordHash("hashed_password")
                .build();
        
        when(saveUserPort.save(any(User.class))).thenReturn(savedUser);
        
        System.out.println("✅ TEST: register_creates_user_and_returns_user");
        System.out.println("📝 Username: " + username);
        System.out.println("📝 Email: " + email);
        System.out.println("📝 Expected: User created with correct username and email");
        
        // Act
        RegisterUseCase.RegisterCommand command = RegisterUseCase.RegisterCommand.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        
        User result = authService.register(command);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername().toString()).isEqualTo(username);
        assertThat(result.getEmail().toString()).isEqualTo(email);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(saveUserPort).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername().toString()).isEqualTo(username);
        assertThat(capturedUser.getEmail().toString()).isEqualTo(email);
        assertThat(capturedUser.getPasswordHash().getValue()).isEqualTo("hashed_password");
        
        System.out.println("✅ TEST PASSED: register_creates_user_and_returns_user");
    }
    
    @Test
    void register_throws_exception_when_username_already_exists() {
        // Arrange
        String username = "existinguser";
        String email = "test@example.com";
        String password = "Password123";
        
        when(loadUserPort.existsByUsername(username)).thenReturn(true);
        
        System.out.println("✅ TEST: register_throws_exception_when_username_already_exists");
        System.out.println("📝 Username: " + username);
        System.out.println("📝 Expected exception: ValidationException");
        System.out.println("📝 Expected message: Username is already taken");
        
        // Act & Assert
        RegisterUseCase.RegisterCommand command = RegisterUseCase.RegisterCommand.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        
        assertThatThrownBy(() -> authService.register(command))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Username is already taken");
        
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(saveUserPort, never()).save(any(User.class));
        
        System.out.println("✅ TEST PASSED: register_throws_exception_when_username_already_exists");
    }
    
    @Test
    void register_throws_exception_when_email_already_exists() {
        // Arrange
        String username = "testuser";
        String email = "existing@example.com";
        String password = "Password123";
        
        when(loadUserPort.existsByUsername(username)).thenReturn(false);
        when(loadUserPort.existsByEmail(email)).thenReturn(true);
        
        System.out.println("✅ TEST: register_throws_exception_when_email_already_exists");
        System.out.println("📝 Email: " + email);
        System.out.println("📝 Expected exception: ValidationException");
        System.out.println("📝 Expected message: Email is already taken");
        
        // Act & Assert
        RegisterUseCase.RegisterCommand command = RegisterUseCase.RegisterCommand.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        
        assertThatThrownBy(() -> authService.register(command))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email is already taken");
        
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(saveUserPort, never()).save(any(User.class));
        
        System.out.println("✅ TEST PASSED: register_throws_exception_when_email_already_exists");
    }
    
    @Test
    void login_returns_token_when_credentials_are_valid() {
        // Arrange
        String usernameOrEmail = "testuser";
        String password = "Password123";
        
        User user = MotherObjects.user().build();
        when(loadUserPort.findByUsernameOrEmail(usernameOrEmail)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(password, user.getPasswordHash())).thenReturn(true);
        when(tokenProviderPort.generateToken(any(), anyString(), any(Boolean.class))).thenReturn("valid_token");
        
        System.out.println("✅ TEST: login_returns_token_when_credentials_are_valid");
        System.out.println("📝 Username/Email: " + usernameOrEmail);
        System.out.println("📝 Expected token: valid_token");
        
        // Act
        LoginUseCase.LoginCommand command = LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(usernameOrEmail)
                .password(password)
                .build();
        
        LoginUseCase.LoginResult result = authService.login(command);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("valid_token");
        verify(tokenProviderPort).generateToken(any(UserId.class), anyString(), any(Boolean.class));
        
        System.out.println("✅ TEST PASSED: login_returns_token_when_credentials_are_valid");
    }
    
    @Test
    void login_throws_exception_when_user_not_found() {
        // Arrange
        String usernameOrEmail = "nonexistent";
        String password = "Password123";
        
        when(loadUserPort.findByUsernameOrEmail(usernameOrEmail)).thenReturn(Optional.empty());
        
        System.out.println("✅ TEST: login_throws_exception_when_user_not_found");
        System.out.println("📝 Username/Email: " + usernameOrEmail);
        System.out.println("📝 Expected exception: UnauthorizedActionException");
        System.out.println("📝 Expected message: Invalid credentials");
        
        // Act & Assert
        LoginUseCase.LoginCommand command = LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(usernameOrEmail)
                .password(password)
                .build();
        
        assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("Invalid credentials");
        
        verifyNoInteractions(passwordEncoderPort, tokenProviderPort);
        
        System.out.println("✅ TEST PASSED: login_throws_exception_when_user_not_found");
    }
    
    @Test
    void login_throws_exception_when_password_is_incorrect() {
        // Arrange
        String usernameOrEmail = "testuser";
        String password = "WrongPassword";
        
        User user = MotherObjects.user().build();
        when(loadUserPort.findByUsernameOrEmail(usernameOrEmail)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(password, user.getPasswordHash())).thenReturn(false);
        
        System.out.println("✅ TEST: login_throws_exception_when_password_is_incorrect");
        System.out.println("📝 Username/Email: " + usernameOrEmail);
        System.out.println("📝 Password: " + password);
        System.out.println("📝 Expected exception: UnauthorizedActionException");
        System.out.println("📝 Expected message: Invalid credentials");
        
        // Act & Assert
        LoginUseCase.LoginCommand command = LoginUseCase.LoginCommand.builder()
                .usernameOrEmail(usernameOrEmail)
                .password(password)
                .build();
        
        assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("Invalid credentials");
        
        verify(passwordEncoderPort).matches(password, user.getPasswordHash());
        verifyNoInteractions(tokenProviderPort);
        
        System.out.println("✅ TEST PASSED: login_throws_exception_when_password_is_incorrect");
    }
}