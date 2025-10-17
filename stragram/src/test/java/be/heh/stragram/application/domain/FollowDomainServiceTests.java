package be.heh.stragram.application.domain;

import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.service.FollowDomainService;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.testutil.MotherObjects;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FollowDomainServiceTests {

    private final FollowDomainService followDomainService = new FollowDomainService();

    @Test
    void createFollowRelationship_should_throw_exception_when_user_tries_to_follow_themselves() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        User user = MotherObjects.user().withId(userId.getValue()).build();

        System.out.println("✅ TEST: createFollowRelationship_should_throw_exception_when_user_tries_to_follow_themselves");
        System.out.println("📝 User ID: " + userId.getValue());
        System.out.println("📝 Expected exception: ValidationException");
        System.out.println("📝 Expected message: Users cannot follow themselves");

        // Act & Assert
        assertThatThrownBy(() -> followDomainService.createFollowRelationship(user, user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Users cannot follow themselves");
        
        System.out.println("✅ TEST PASSED: createFollowRelationship_should_throw_exception_when_user_tries_to_follow_themselves");
    }

    @Test
    void createFollowRelationship_should_increment_follower_and_following_counts() {
        // Arrange
        User follower = MotherObjects.user()
                .withFollowingCount(5)
                .build();
        
        User following = MotherObjects.user()
                .withFollowersCount(10)
                .build();

        System.out.println("✅ TEST: createFollowRelationship_should_increment_follower_and_following_counts");
        System.out.println("📝 Initial follower following count: " + 5);
        System.out.println("📝 Initial following followers count: " + 10);
        System.out.println("📝 Expected follower following count: " + 6);
        System.out.println("📝 Expected following followers count: " + 11);

        // Act
        FollowRelationship relationship = followDomainService.createFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(6);
        assertThat(following.getFollowersCount()).isEqualTo(11);
        assertThat(relationship.getFollowerId()).isEqualTo(follower.getId());
        assertThat(relationship.getFollowingId()).isEqualTo(following.getId());
        
        System.out.println("✅ TEST PASSED: createFollowRelationship_should_increment_follower_and_following_counts");
    }

    @Test
    void removeFollowRelationship_should_decrement_follower_and_following_counts() {
        // Arrange
        User follower = MotherObjects.user()
                .withFollowingCount(5)
                .build();
        
        User following = MotherObjects.user()
                .withFollowersCount(10)
                .build();

        System.out.println("✅ TEST: removeFollowRelationship_should_decrement_follower_and_following_counts");
        System.out.println("📝 Initial follower following count: " + 5);
        System.out.println("📝 Initial following followers count: " + 10);
        System.out.println("📝 Expected follower following count: " + 4);
        System.out.println("📝 Expected following followers count: " + 9);

        // Act
        followDomainService.removeFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(4);
        assertThat(following.getFollowersCount()).isEqualTo(9);
        
        System.out.println("✅ TEST PASSED: removeFollowRelationship_should_decrement_follower_and_following_counts");
    }

    @Test
    void removeFollowRelationship_should_not_decrement_below_zero() {
        // Arrange
        User follower = MotherObjects.user()
                .withFollowingCount(0)
                .build();
        
        User following = MotherObjects.user()
                .withFollowersCount(0)
                .build();

        System.out.println("✅ TEST: removeFollowRelationship_should_not_decrement_below_zero");
        System.out.println("📝 Initial follower following count: " + 0);
        System.out.println("📝 Initial following followers count: " + 0);
        System.out.println("📝 Expected follower following count: " + 0);
        System.out.println("📝 Expected following followers count: " + 0);

        // Act
        followDomainService.removeFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(0);
        assertThat(following.getFollowersCount()).isEqualTo(0);
        
        System.out.println("✅ TEST PASSED: removeFollowRelationship_should_not_decrement_below_zero");
    }
}