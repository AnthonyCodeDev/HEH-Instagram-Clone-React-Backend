package be.heh.stragram.application.domain.model;

import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.Username;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class User {
    private final UserId id;
    private Username username;
    private Email email;
    private PasswordHash passwordHash;
    
    @Setter
    private String bio;
    
    @Setter
    private String avatarUrl;

    @Setter
    private String name;

    @Setter
    private String bannerUrl;

    @Setter
    private String phone;

    @Setter
    private String location;

    @Setter
    private LocalDate birthdate;

    // Social links
    @Setter
    private String tiktok;

    @Setter
    private String twitter;

    @Setter
    private String youtube;
    
    @Setter(AccessLevel.PACKAGE)
    private int followersCount;
    
    @Setter(AccessLevel.PACKAGE)
    private int followingCount;
    
    private final Instant createdAt;
    
    @Setter(AccessLevel.PACKAGE)
    private Instant updatedAt;
    
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    private User(
            UserId id,
            Username username,
            Email email,
            PasswordHash passwordHash,
            String bio,
            String avatarUrl,
            String name,
            String bannerUrl,
            String phone,
            String location,
            LocalDate birthdate,
            String tiktok,
            String twitter,
            String youtube,
            int followersCount,
            int followingCount,
            Instant createdAt,
            Instant updatedAt,
            Role role
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.bannerUrl = bannerUrl;
        this.phone = phone;
        this.location = location;
        this.birthdate = birthdate;
        this.tiktok = tiktok;
        this.twitter = twitter;
        this.youtube = youtube;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
    }

    public static User create(
            Username username,
            Email email,
            PasswordHash passwordHash
    ) {
        return new User(
                UserId.generate(),
                username,
                email,
                passwordHash,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                Instant.now(),
                Instant.now(),
                Role.USER
        );
    }

    public static User reconstitute(
            UserId id,
            Username username,
            Email email,
            PasswordHash passwordHash,
            String bio,
            String avatarUrl,
            String name,
            String bannerUrl,
            String phone,
            String location,
            LocalDate birthdate,
            String tiktok,
            String twitter,
            String youtube,
            int followersCount,
            int followingCount,
            Instant createdAt,
            Instant updatedAt,
            Role role
    ) {
        return new User(
                id,
                username,
                email,
                passwordHash,
                bio,
                avatarUrl,
                name,
                bannerUrl,
                phone,
                location,
                birthdate,
                tiktok,
                twitter,
                youtube,
                followersCount,
                followingCount,
                createdAt,
                updatedAt,
                role
        );
    }

    public void updateProfile(Username newUsername, Email newEmail, String newBio, String newAvatarUrl,
                              String newName, String newBannerUrl, String newPhone, String newLocation,
                              LocalDate newBirthdate, String newTiktok, String newTwitter, String newYoutube) {
        this.username = newUsername;
        this.email = newEmail;
        this.bio = newBio;
        this.avatarUrl = newAvatarUrl;
        this.name = newName;
        this.bannerUrl = newBannerUrl;
        this.phone = newPhone;
        this.location = newLocation;
        this.birthdate = newBirthdate;
        this.tiktok = newTiktok;
        this.twitter = newTwitter;
        this.youtube = newYoutube;
        this.updatedAt = Instant.now();
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
        this.updatedAt = Instant.now();
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void incrementFollowersCount() {
        this.followersCount++;
        this.updatedAt = Instant.now();
    }

    public void decrementFollowersCount() {
        if (this.followersCount > 0) {
            this.followersCount--;
            this.updatedAt = Instant.now();
        }
    }

    public void incrementFollowingCount() {
        this.followingCount++;
        this.updatedAt = Instant.now();
    }

    public void decrementFollowingCount() {
        if (this.followingCount > 0) {
            this.followingCount--;
            this.updatedAt = Instant.now();
        }
    }

    public void validateBioLength(int maxLength) {
        if (bio != null && bio.length() > maxLength) {
            throw new ValidationException("Bio cannot exceed " + maxLength + " characters");
        }
    }
}
