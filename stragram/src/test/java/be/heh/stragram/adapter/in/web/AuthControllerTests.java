package be.heh.stragram.adapter.in.web;

import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.LoginUseCase;
import be.heh.stragram.application.port.in.RegisterUseCase;
import be.heh.stragram.testutil.MotherObjects;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUseCase registerUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

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

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .with(csrf())
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
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void register_should_return_400_when_validation_fails() throws Exception {
        // Arrange
        when(registerUseCase.register(any(RegisterUseCase.RegisterCommand.class)))
                .thenThrow(new ValidationException("Username is already taken"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "existinguser",
                            "email": "test@example.com",
                            "password": "Password123"
                        }
                        """))
                .andExpect(status().isBadRequest());
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

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "usernameOrEmail": "testuser",
                            "password": "Password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }
}
