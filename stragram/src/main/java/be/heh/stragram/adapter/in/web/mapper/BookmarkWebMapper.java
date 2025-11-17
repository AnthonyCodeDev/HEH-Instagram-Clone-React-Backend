package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.BookmarkDtos;
import be.heh.stragram.application.domain.model.Bookmark;
import org.springframework.stereotype.Component;

@Component
public class BookmarkWebMapper {

    public BookmarkDtos.BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        return BookmarkDtos.BookmarkResponse.builder()
                .id(bookmark.getId().toString())
                .userId(bookmark.getUserId().toString())
                .postId(bookmark.getPostId().toString())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
