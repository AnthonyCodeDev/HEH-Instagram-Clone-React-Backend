package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.ListRecentPostsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recent-posts")
@RequiredArgsConstructor
public class RecentPostsController {

    private final ListRecentPostsQuery listRecentPostsQuery;
    private final PostWebMapper postWebMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDtos.PostListResponse> getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        List<Post> posts = listRecentPostsQuery.listRecentPosts(page, size, currentUserId);
        
        List<PostDtos.PostResponse> postResponses = posts.stream()
                .map(post -> postWebMapper.toPostResponse(post, currentUserId))
                .collect(Collectors.toList());

        PostDtos.PostListResponse response = PostDtos.PostListResponse.builder()
                .posts(postResponses)
                .page(page)
                .size(size)
                .hasMore(postResponses.size() == size)
                .build();

        return ResponseEntity.ok(response);
    }
}
