package com.example.backendstragram.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String imageUrl;
    private String caption;
    private String createdAt;
    private UserResponse user;
    private int likes;
    private boolean isLikedByCurrentUser;
}
