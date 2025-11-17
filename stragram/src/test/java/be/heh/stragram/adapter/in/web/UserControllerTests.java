package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.UserDtos;
import be.heh.stragram.adapter.in.web.mapper.UserWebMapper;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.*;
import be.heh.stragram.application.port.out.ImageStoragePort;
import be.heh.stragram.testutil.MotherObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserController - Page Settings
 * Teste les endpoints: GET /users/me, PUT /users/profile
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private GetUserProfileQuery getUserProfileQuery;

    @Mock
    private UpdateUserProfileCommand updateUserProfileCommand;

    @Mock
    private DeleteUserCommand deleteUserCommand;

    @Mock
    private SearchUsersQuery searchUsersQuery;

    @Mock
    private ListRandomUsersQuery listRandomUsersQuery;

    @Mock
    private FollowUserUseCase followUserUseCase;

    @Mock
    private UnfollowUserUseCase unfollowUserUseCase;

    @Mock
    private ImageStoragePort imageStoragePort;

    @Mock
    private ChangePasswordUseCase changePasswordUseCase;

    @Mock
    private be.heh.stragram.application.port.in.ListFollowersQuery listFollowersQuery;

    @Mock
    private be.heh.stragram.application.port.in.ListFollowingQuery listFollowingQuery;

    @Mock
    private UserWebMapper userWebMapper;

    private UserController userController;
    private UserId testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userController = new UserController(
                getUserProfileQuery,
                updateUserProfileCommand,
                deleteUserCommand,
                searchUsersQuery,
                listRandomUsersQuery,
                followUserUseCase,
                unfollowUserUseCase,
                imageStoragePort,
                changePasswordUseCase,
                listFollowersQuery,
                listFollowingQuery,
                userWebMapper
        );

        testUserId = UserId.of(UUID.randomUUID());
        testUser = MotherObjects.user()
                .withUserId(testUserId)
                .withUsername("testuser")
                .withEmail("test@test.com")
                .withAvatarUrl("http://localhost:8081/images/avatar.png")
                .withBannerUrl("http://localhost:8081/images/banner.png")
                .withBio("Test bio")
                .build();
    }

    @Test
    void getUserProfile_with_me_returns_current_user_profile() {
        // Arrange
        when(getUserProfileQuery.getUserById(testUserId)).thenReturn(testUser);
        UserDtos.UserResponse expectedResponse = createUserResponse();
        when(userWebMapper.toUserResponse(testUser, testUserId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserDtos.UserResponse> response = userController.getUserProfile("me", testUserId);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(getUserProfileQuery).getUserById(testUserId);
        verify(userWebMapper).toUserResponse(testUser, testUserId);
    }

    @Test
    void getUserProfile_with_me_returns_401_when_not_authenticated() {
        // Act
        ResponseEntity<UserDtos.UserResponse> response = userController.getUserProfile("me", null);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
        verifyNoInteractions(getUserProfileQuery);
    }

    @Test
    void updateMyProfile_preserves_avatarUrl_when_not_provided() {
        // Arrange
        User existingUser = MotherObjects.user()
                .withUserId(testUserId)
                .withAvatarUrl("http://localhost:8081/images/existing-avatar.png")
                .withBannerUrl("http://localhost:8081/images/existing-banner.png")
                .build();

        UserDtos.UpdateUserRequest request = UserDtos.UpdateUserRequest.builder()
                .username("updateduser")
                .email("updated@test.com")
                .bio("Updated bio")
                // avatarUrl et bannerUrl NOT provided
                .build();

        User updatedUser = MotherObjects.user()
                .withUserId(testUserId)
                .withUsername("updateduser")
                .withEmail("updated@test.com")
                .withBio("Updated bio")
                .withAvatarUrl("http://localhost:8081/images/existing-avatar.png") // preserved!
                .withBannerUrl("http://localhost:8081/images/existing-banner.png") // preserved!
                .build();

        when(getUserProfileQuery.getUserById(testUserId)).thenReturn(existingUser);
        when(updateUserProfileCommand.update(eq(testUserId), any())).thenReturn(updatedUser);
        when(userWebMapper.toUserResponse(updatedUser, testUserId)).thenReturn(createUpdatedUserResponse());

        // Act
        ResponseEntity<UserDtos.UserResponse> response = userController.updateMyProfile(request, testUserId);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        
        ArgumentCaptor<UpdateUserProfileCommand.UpdateProfileCommand> commandCaptor = 
                ArgumentCaptor.forClass(UpdateUserProfileCommand.UpdateProfileCommand.class);
        verify(updateUserProfileCommand).update(eq(testUserId), commandCaptor.capture());
        
        UpdateUserProfileCommand.UpdateProfileCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.getAvatarUrl()).isEqualTo("http://localhost:8081/images/existing-avatar.png");
        assertThat(capturedCommand.getBannerUrl()).isEqualTo("http://localhost:8081/images/existing-banner.png");
    }

    @Test
    void updateMyProfile_preserves_bannerUrl_when_not_provided() {
        // Arrange
        User existingUser = MotherObjects.user()
                .withUserId(testUserId)
                .withBannerUrl("http://localhost:8081/images/existing-banner.png")
                .build();

        UserDtos.UpdateUserRequest request = UserDtos.UpdateUserRequest.builder()
                .username("testuser")
                .avatarUrl("http://localhost:8081/images/new-avatar.png")
                // bannerUrl NOT provided
                .build();

        when(getUserProfileQuery.getUserById(testUserId)).thenReturn(existingUser);
        when(updateUserProfileCommand.update(eq(testUserId), any())).thenReturn(existingUser);
        when(userWebMapper.toUserResponse(any(), eq(testUserId))).thenReturn(createUserResponse());

        // Act
        userController.updateMyProfile(request, testUserId);

        // Assert
        ArgumentCaptor<UpdateUserProfileCommand.UpdateProfileCommand> commandCaptor = 
                ArgumentCaptor.forClass(UpdateUserProfileCommand.UpdateProfileCommand.class);
        verify(updateUserProfileCommand).update(eq(testUserId), commandCaptor.capture());
        
        assertThat(commandCaptor.getValue().getBannerUrl()).isEqualTo("http://localhost:8081/images/existing-banner.png");
    }



    // Helper methods
    private UserDtos.UserResponse createUserResponse() {
        return UserDtos.UserResponse.builder()
                .id(testUserId.getValue().toString())
                .username("testuser")
                .email("test@test.com")
                .avatarUrl("http://localhost:8081/images/avatar.png")
                .bannerUrl("http://localhost:8081/images/banner.png")
                .bio("Test bio")
                .socialLinks(new HashMap<>())
                .build();
    }

    private UserDtos.UserResponse createUpdatedUserResponse() {
        return UserDtos.UserResponse.builder()
                .id(testUserId.getValue().toString())
                .username("updateduser")
                .email("updated@test.com")
                .bio("Updated bio")
                .avatarUrl("http://localhost:8081/images/existing-avatar.png")
                .bannerUrl("http://localhost:8081/images/existing-banner.png")
                .socialLinks(new HashMap<>())
                .build();
    }
}
