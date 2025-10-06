package com.example.backendstragram.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String profilePictureUrl;
    private String bio;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private boolean isPrivate;
}
