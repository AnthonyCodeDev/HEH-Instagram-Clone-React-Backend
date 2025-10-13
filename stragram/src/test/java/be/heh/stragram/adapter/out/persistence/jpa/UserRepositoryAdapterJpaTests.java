package be.heh.stragram.adapter.out.persistence.jpa;

import be.heh.stragram.adapter.out.persistence.jpa.adapter.UserRepositoryAdapter;
import be.heh.stragram.adapter.out.persistence.jpa.entity.UserJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.UserJpaMapper;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.Username;
import be.heh.stragram.testutil.MotherObjects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import({UserJpaMapper.class, UserRepositoryAdapter.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryAdapterJpaTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void save_and_find_by_id_should_work() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = MotherObjects.user()
                .withId(userId)
                .withUsername("testuser")
                .withEmail("test@example.com")
                .build();

        // Act
        User savedUser = userRepositoryAdapter.save(user);
        Optional<User> foundUser = userRepositoryAdapter.findById(UserId.of(userId));

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId().getValue()).isEqualTo(userId);
        assertThat(foundUser.get().getUsername().toString()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail().toString()).isEqualTo("test@example.com");
    }

    @Test
    void find_by_username_should_work() {
        // Arrange
        String username = "usernametest";
        User user = MotherObjects.user()
                .withUsername(username)
                .build();

        userRepositoryAdapter.save(user);

        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByUsername(username);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername().toString()).isEqualTo(username);
    }

    @Test
    void find_by_email_should_work() {
        // Arrange
        String email = "email@test.com";
        User user = MotherObjects.user()
                .withEmail(email)
                .build();

        userRepositoryAdapter.save(user);

        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByEmail(email);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail().toString()).isEqualTo(email);
    }

    @Test
    void exists_by_username_should_return_true_when_username_exists() {
        // Arrange
        String username = "existinguser";
        User user = MotherObjects.user()
                .withUsername(username)
                .build();

        userRepositoryAdapter.save(user);

        // Act
        boolean exists = userRepositoryAdapter.existsByUsername(username);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void exists_by_email_should_return_true_when_email_exists() {
        // Arrange
        String email = "existing@example.com";
        User user = MotherObjects.user()
                .withEmail(email)
                .build();

        userRepositoryAdapter.save(user);

        // Act
        boolean exists = userRepositoryAdapter.existsByEmail(email);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void delete_should_remove_user() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = MotherObjects.user()
                .withId(userId)
                .build();

        User savedUser = userRepositoryAdapter.save(user);

        // Act
        userRepositoryAdapter.delete(savedUser);
        Optional<User> foundUser = userRepositoryAdapter.findById(UserId.of(userId));

        // Assert
        assertThat(foundUser).isEmpty();
    }
}
