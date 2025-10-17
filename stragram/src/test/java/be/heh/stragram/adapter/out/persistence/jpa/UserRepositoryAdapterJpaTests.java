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

        System.out.println("✅ TEST: save_and_find_by_id_should_work");
        System.out.println("📝 Expected userId: " + userId);
        System.out.println("📝 Expected username: testuser");
        System.out.println("📝 Expected email: test@example.com");

        // Act
        User savedUser = userRepositoryAdapter.save(user);
        Optional<User> foundUser = userRepositoryAdapter.findById(UserId.of(userId));

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId().getValue()).isEqualTo(userId);
        assertThat(foundUser.get().getUsername().toString()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail().toString()).isEqualTo("test@example.com");
        
        System.out.println("✅ TEST PASSED: save_and_find_by_id_should_work");
    }

    @Test
    void find_by_username_should_work() {
        // Arrange
        String username = "usernametest";
        User user = MotherObjects.user()
                .withUsername(username)
                .build();

        System.out.println("✅ TEST: find_by_username_should_work");
        System.out.println("📝 Expected username: " + username);

        userRepositoryAdapter.save(user);

        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByUsername(username);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername().toString()).isEqualTo(username);
        
        System.out.println("✅ TEST PASSED: find_by_username_should_work");
    }

    @Test
    void find_by_email_should_work() {
        // Arrange
        String email = "email@test.com";
        User user = MotherObjects.user()
                .withEmail(email)
                .build();

        System.out.println("✅ TEST: find_by_email_should_work");
        System.out.println("📝 Expected email: " + email);

        userRepositoryAdapter.save(user);

        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByEmail(email);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail().toString()).isEqualTo(email);
        
        System.out.println("✅ TEST PASSED: find_by_email_should_work");
    }

    @Test
    void exists_by_username_should_return_true_when_username_exists() {
        // Arrange
        String username = "existinguser";
        User user = MotherObjects.user()
                .withUsername(username)
                .build();

        System.out.println("✅ TEST: exists_by_username_should_return_true_when_username_exists");
        System.out.println("📝 Expected username: " + username);
        System.out.println("📝 Expected exists: true");

        userRepositoryAdapter.save(user);

        // Act
        boolean exists = userRepositoryAdapter.existsByUsername(username);

        // Assert
        assertThat(exists).isTrue();
        
        System.out.println("✅ TEST PASSED: exists_by_username_should_return_true_when_username_exists");
    }

    @Test
    void exists_by_email_should_return_true_when_email_exists() {
        // Arrange
        String email = "existing@example.com";
        User user = MotherObjects.user()
                .withEmail(email)
                .build();

        System.out.println("✅ TEST: exists_by_email_should_return_true_when_email_exists");
        System.out.println("📝 Expected email: " + email);
        System.out.println("📝 Expected exists: true");

        userRepositoryAdapter.save(user);

        // Act
        boolean exists = userRepositoryAdapter.existsByEmail(email);

        // Assert
        assertThat(exists).isTrue();
        
        System.out.println("✅ TEST PASSED: exists_by_email_should_return_true_when_email_exists");
    }

    @Test
    void delete_should_remove_user() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = MotherObjects.user()
                .withId(userId)
                .build();

        User savedUser = userRepositoryAdapter.save(user);

        System.out.println("✅ TEST: delete_should_remove_user");
        System.out.println("📝 User ID to delete: " + userId);
        System.out.println("📝 Expected result: user not found after deletion");

        // Act
        userRepositoryAdapter.delete(savedUser);
        Optional<User> foundUser = userRepositoryAdapter.findById(UserId.of(userId));

        // Assert
        assertThat(foundUser).isEmpty();
        
        System.out.println("✅ TEST PASSED: delete_should_remove_user");
    }
}