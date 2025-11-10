package be.heh.stragram.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "name")
    private String name;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "phone")
    private String phone;

    @Column(name = "location")
    private String location;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    // Social media columns (from V4, V6 removed instagram)
    @Column(name = "tiktok")
    private String tiktok;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "youtube")
    private String youtube;

    @Column(name = "followers_count", nullable = false)
    private int followersCount;

    @Column(name = "following_count", nullable = false)
    private int followingCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public enum UserRole {
        USER, ADMIN
    }
}
