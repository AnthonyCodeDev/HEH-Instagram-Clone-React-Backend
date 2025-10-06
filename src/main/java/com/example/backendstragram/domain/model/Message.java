package com.example.backendstragram.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private User sender;
    private User recipient;
    private boolean read;
}
