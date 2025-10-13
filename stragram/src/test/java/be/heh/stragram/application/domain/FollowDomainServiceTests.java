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

        // Act & Assert
        assertThatThrownBy(() -> followDomainService.createFollowRelationship(user, user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Users cannot follow themselves");
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

        // Act
        FollowRelationship relationship = followDomainService.createFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(6);
        assertThat(following.getFollowersCount()).isEqualTo(11);
        assertThat(relationship.getFollowerId()).isEqualTo(follower.getId());
        assertThat(relationship.getFollowingId()).isEqualTo(following.getId());
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

        // Act
        followDomainService.removeFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(4);
        assertThat(following.getFollowersCount()).isEqualTo(9);
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

        // Act
        followDomainService.removeFollowRelationship(follower, following);

        // Assert
        assertThat(follower.getFollowingCount()).isEqualTo(0);
        assertThat(following.getFollowersCount()).isEqualTo(0);
    }
}
