package be.heh.stragram.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RandomUserDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RandomUserResponse {
        private String id;
        private String name;       // display name - here we use username if no dedicated name
        private String username;
        private String avatarUrl;  // image
        private String bannerUrl;  // may be null if not present
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RandomUserListResponse {
        private List<RandomUserResponse> users;
        private int size;
    }
}
