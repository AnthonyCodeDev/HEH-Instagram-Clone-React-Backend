package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.UserDtos;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.FollowPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWebMapper {

    private final FollowPort followPort;

    public UserDtos.UserResponse toUserResponse(User user, UserId currentUserId) {
        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = followPort.exists(currentUserId, user.getId());
        }

        return UserDtos.UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername().toString())
                .email(user.getEmail().toString())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .createdAt(user.getCreatedAt())
                .isCurrentUserFollowing(isFollowing)
                .build();
    }

    public UserDtos.SearchUserResponseItem toSearchUserResponseItem(User user, UserId currentUserId) {
        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = followPort.exists(currentUserId, user.getId());
        }

        return UserDtos.SearchUserResponseItem.builder()
                .id(user.getId().toString())
                .username(user.getUsername().toString())
                .avatarUrl(user.getAvatarUrl())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .isCurrentUserFollowing(isFollowing)
                .build();
    }

    public UserDtos.FollowResponse toFollowResponse(FollowRelationship followRelationship) {
        return UserDtos.FollowResponse.builder()
                .followerId(followRelationship.getFollowerId().toString())
                .followingId(followRelationship.getFollowingId().toString())
                .createdAt(followRelationship.getCreatedAt())
                .build();
    }
}
