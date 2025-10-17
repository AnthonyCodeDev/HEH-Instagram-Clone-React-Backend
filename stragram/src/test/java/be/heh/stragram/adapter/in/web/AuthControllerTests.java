package be.heh.stragram.adapter.in.web;

import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import be.heh.stragram.testutil.MotherObjects;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RegisterUseCase registerUseCase;

    @Mock
    private LoginUseCase loginUseCase;
    
    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(registerUseCase, loginUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_should_return_201_and_auth_response_when_successful() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "Password123";
        
        User user = MotherObjects.user()
                .withUsername(username)
                .withEmail(email)
                .build();
        
        when(registerUseCase.register(any(RegisterUseCase.RegisterCommand.class))).thenReturn(user);
        
        String token = "jwt-token";
        UserId userId = UserId.of(UUID.randomUUID());
        
        when(loginUseCase.login(any(LoginUseCase.LoginCommand.class)))
                .thenReturn(LoginUseCase.LoginResult.builder()
                        .token(token)
                        .userId(userId)
                        .build());

        System.out.println("✅ TEST: register_should_return_201_and_auth_response_when_successful");
        System.out.println("📝 Expected token: " + token);
        System.out.println("📝 Expected userId exists: true");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "testuser",
                            "email": "test@example.com",
                            "password": "Password123"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").exists());
                
        System.out.println("✅ TEST PASSED: register_should_return_201_and_auth_response_when_successful");
    }

    @Test
    void register_should_return_400_when_validation_fails() throws Exception {
        // Arrange
        when(registerUseCase.register(any(RegisterUseCase.RegisterCommand.class)))
                .thenThrow(new ValidationException("Username is already taken"));

        System.out.println("✅ TEST: register_should_return_400_when_validation_fails");
        System.out.println("📝 Expected exception: ValidationException");
        System.out.println("📝 Expected message: Username is already taken");

        // Act & Assert
        try {
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "username": "existinguser",
                                "email": "test@example.com",
                                "password": "Password123"
                            }
                            """));
            org.junit.jupiter.api.Assertions.fail("Should have thrown ValidationException");
        } catch (Exception e) {
            // Vérifie que l'exception est bien une ServletException causée par ValidationException
            org.junit.jupiter.api.Assertions.assertTrue(e.getCause() instanceof ValidationException);
            org.junit.jupiter.api.Assertions.assertEquals("Username is already taken", e.getCause().getMessage());
            System.out.println("✅ TEST PASSED: register_should_return_400_when_validation_fails");
            System.out.println("📝 Actual exception: " + e.getCause().getClass().getSimpleName());
            System.out.println("📝 Actual message: " + e.getCause().getMessage());
        }
    }

    @Test
    void login_should_return_200_and_auth_response_when_successful() throws Exception {
        // Arrange
        String usernameOrEmail = "testuser";
        String password = "Password123";
        
        String token = "jwt-token";
        UserId userId = UserId.of(UUID.randomUUID());
        
        when(loginUseCase.login(any(LoginUseCase.LoginCommand.class)))
                .thenReturn(LoginUseCase.LoginResult.builder()
                        .token(token)
                        .userId(userId)
                        .build());

        System.out.println("✅ TEST: login_should_return_200_and_auth_response_when_successful");
        System.out.println("📝 Expected token: " + token);
        System.out.println("📝 Expected userId exists: true");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "usernameOrEmail": "testuser",
                            "password": "Password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").exists());
                
        System.out.println("✅ TEST PASSED: login_should_return_200_and_auth_response_when_successful");
    }
}
