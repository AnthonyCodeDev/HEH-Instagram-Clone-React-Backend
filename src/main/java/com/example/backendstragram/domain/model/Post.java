package com.example.backendstragram.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String imageUrl;
    private String caption;
    private LocalDateTime createdAt;
    private User user;
    private int likes;
}
