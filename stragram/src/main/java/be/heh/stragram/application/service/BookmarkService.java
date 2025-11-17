package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.Bookmark;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.BookmarkPostUseCase;
import be.heh.stragram.application.port.in.ListBookmarksQuery;
import be.heh.stragram.application.port.out.BookmarkPort;
import be.heh.stragram.application.port.out.LoadPostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService implements BookmarkPostUseCase, ListBookmarksQuery {

    private final BookmarkPort bookmarkPort;
    private final LoadPostPort loadPostPort;

    @Override
    @Transactional
    public Bookmark bookmarkPost(UserId userId, PostId postId) {
        // Verify post exists
        loadPostPort.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post", postId.toString()));

        // Check if already bookmarked - return existing bookmark (idempotent)
        var existingBookmark = bookmarkPort.findByUserIdAndPostId(userId, postId);
        if (existingBookmark.isPresent()) {
            return existingBookmark.get();
        }

        // Create new bookmark
        Bookmark bookmark = Bookmark.create(userId, postId);
        return bookmarkPort.save(bookmark);
    }

    @Override
    @Transactional
    public void unbookmarkPost(UserId userId, PostId postId) {
        // Verify bookmark exists
        if (!bookmarkPort.existsByUserIdAndPostId(userId, postId)) {
            throw new NotFoundException("Bookmark not found");
        }
        
        bookmarkPort.delete(userId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> listBookmarkedPosts(UserId userId, int page, int size) {
        if (size <= 0 || size > 100) {
            throw new ValidationException("Size must be between 1 and 100");
        }
        
        return bookmarkPort.findBookmarkedPostsByUserId(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasMore(UserId userId, int page, int size) {
        long total = bookmarkPort.countByUserId(userId);
        return (long) (page + 1) * size < total;
    }

    @Override
    @Transactional(readOnly = true)
    public long countBookmarks(UserId userId) {
        return bookmarkPort.countByUserId(userId);
    }
}
