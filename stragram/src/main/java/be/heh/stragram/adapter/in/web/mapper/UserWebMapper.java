package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.UserDtos;
import be.heh.stragram.adapter.in.web.dto.RandomUserDtos;
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

        // Build social links map
        java.util.Map<String, String> socialLinks = new java.util.HashMap<>();
        if (user.getTiktok() != null) socialLinks.put("tiktok", user.getTiktok());
        if (user.getTwitter() != null) socialLinks.put("twitter", user.getTwitter());
        if (user.getYoutube() != null) socialLinks.put("youtube", user.getYoutube());

        return UserDtos.UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername().toString())
                .name(user.getName())
                .email(user.getEmail().toString())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .bannerUrl(user.getBannerUrl())
                .phone(user.getPhone())
                .location(user.getLocation())
                .birthdate(user.getBirthdate() != null ? user.getBirthdate().toString() : null)
                .socialLinks(socialLinks)
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

    public RandomUserDtos.RandomUserResponse toRandomUserResponse(User user) {
        // Use name if available, otherwise fallback to username
        String displayName = user.getName() != null ? user.getName() : user.getUsername().toString();
        return RandomUserDtos.RandomUserResponse.builder()
                .id(user.getId().toString())
                .name(displayName)
                .username(user.getUsername().toString())
                .avatarUrl(user.getAvatarUrl())
                .bannerUrl(user.getBannerUrl())
                .build();
    }

    public UserDtos.FollowerItem toFollowerItem(User user, UserId currentUserId) {
        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            isFollowing = followPort.exists(currentUserId, user.getId());
        }

        return UserDtos.FollowerItem.builder()
                .id(user.getId().toString())
                .username(user.getUsername().toString())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .isCurrentUserFollowing(isFollowing)
                .build();
    }

    public UserDtos.FollowingItem toFollowingItem(User user, UserId currentUserId) {
        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            isFollowing = followPort.exists(currentUserId, user.getId());
        }

        return UserDtos.FollowingItem.builder()
                .id(user.getId().toString())
                .username(user.getUsername().toString())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .isCurrentUserFollowing(isFollowing)
                .build();
    }
}
