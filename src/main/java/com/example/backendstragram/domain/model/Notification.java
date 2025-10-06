package com.example.backendstragram.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private String type; // LIKE, COMMENT, FOLLOW
    private User fromUser;
    private User toUser;
    private Post relatedPost;
    private LocalDateTime createdAt;
    private boolean read;
}
