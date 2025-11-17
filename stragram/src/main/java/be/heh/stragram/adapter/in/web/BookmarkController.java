package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.BookmarkDtos;
import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.adapter.in.web.mapper.BookmarkWebMapper;
import be.heh.stragram.adapter.in.web.mapper.PostWebMapper;
import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.BookmarkPostUseCase;
import be.heh.stragram.application.port.in.ListBookmarksQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkPostUseCase bookmarkPostUseCase;
    private final ListBookmarksQuery listBookmarksQuery;
    private final BookmarkWebMapper bookmarkWebMapper;
    private final PostWebMapper postWebMapper;

    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<BookmarkDtos.BookmarkResponse> bookmarkPost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Bookmark bookmark = bookmarkPostUseCase.bookmarkPost(
                currentUserId,
                PostId.fromString(postId)
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarkWebMapper.toBookmarkResponse(bookmark));
    }

    @DeleteMapping("/{postId}/bookmark")
    public ResponseEntity<Void> unbookmarkPost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        bookmarkPostUseCase.unbookmarkPost(
                currentUserId,
                PostId.fromString(postId)
        );
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkDtos.BookmarkedPostsResponse> getBookmarkedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserId currentUserId) {
        
        List<Post> posts = listBookmarksQuery.listBookmarkedPosts(currentUserId, page, size);
        boolean hasMore = listBookmarksQuery.hasMore(currentUserId, page, size);
        long totalBookmarks = listBookmarksQuery.countBookmarks(currentUserId);
        
        List<PostDtos.PostResponse> postResponses = posts.stream()
                .map(post -> postWebMapper.toPostResponse(post, currentUserId))
                .collect(Collectors.toList());
        
        BookmarkDtos.BookmarkedPostsResponse response = BookmarkDtos.BookmarkedPostsResponse.builder()
                .posts(postResponses)
                .page(page)
                .size(size)
                .hasMore(hasMore)
                .totalBookmarks(totalBookmarks)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookmarks/count")
    public ResponseEntity<Long> getBookmarksCount(
            @AuthenticationPrincipal UserId currentUserId) {
        long count = listBookmarksQuery.countBookmarks(currentUserId);
        return ResponseEntity.ok(count);
    }
}
