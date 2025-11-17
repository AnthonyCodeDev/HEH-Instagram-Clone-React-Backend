package be.heh.stragram.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

public class BookmarkDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkResponse {
        private String id;
        private String userId;
        private String postId;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedPostsResponse {
        private List<PostDtos.PostResponse> posts;
        private int page;
        private int size;
        private boolean hasMore;
        private long totalBookmarks;
    }
}
