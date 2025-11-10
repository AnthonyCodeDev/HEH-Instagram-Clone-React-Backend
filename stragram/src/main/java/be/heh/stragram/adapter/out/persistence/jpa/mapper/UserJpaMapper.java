package be.heh.stragram.adapter.out.persistence.jpa.mapper;

import be.heh.stragram.adapter.out.persistence.jpa.entity.UserJpaEntity;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.Username;
import org.springframework.stereotype.Component;

@Component
public class UserJpaMapper {

    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                Username.of(entity.getUsername()),
                Email.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()),
                entity.getBio(),
                entity.getAvatarUrl(),
                entity.getName(),
                entity.getBannerUrl(),
                entity.getPhone(),
                entity.getLocation(),
                entity.getBirthdate(),
                entity.getTiktok(),
                entity.getTwitter(),
                entity.getYoutube(),
                entity.getFollowersCount(),
                entity.getFollowingCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                mapRole(entity.getRole())
        );
    }

    public UserJpaEntity toEntity(User domain) {
        return UserJpaEntity.builder()
                .id(domain.getId().getValue())
                .username(domain.getUsername().toString())
                .email(domain.getEmail().toString())
                .passwordHash(domain.getPasswordHash().getValue())
                .bio(domain.getBio())
                .avatarUrl(domain.getAvatarUrl())
                .name(domain.getName())
                .bannerUrl(domain.getBannerUrl())
                .phone(domain.getPhone())
                .location(domain.getLocation())
                .birthdate(domain.getBirthdate())
                .tiktok(domain.getTiktok())
                .twitter(domain.getTwitter())
                .youtube(domain.getYoutube())
                .followersCount(domain.getFollowersCount())
                .followingCount(domain.getFollowingCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .role(mapRole(domain.isAdmin()))
                .build();
    }

    private User.Role mapRole(UserJpaEntity.UserRole role) {
        return UserJpaEntity.UserRole.ADMIN.equals(role) ? User.Role.ADMIN : User.Role.USER;
    }

    private UserJpaEntity.UserRole mapRole(boolean isAdmin) {
        return isAdmin ? UserJpaEntity.UserRole.ADMIN : UserJpaEntity.UserRole.USER;
    }
}
