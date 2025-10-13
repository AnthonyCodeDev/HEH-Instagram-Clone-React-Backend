package be.heh.stragram.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "likes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeJpaEntity {

    @EmbeddedId
    private LikeId id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeId {
        @Column(name = "post_id")
        private UUID postId;

        @Column(name = "user_id")
        private UUID userId;
    }
}
