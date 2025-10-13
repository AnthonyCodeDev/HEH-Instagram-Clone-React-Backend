package be.heh.stragram.adapter.in.web.mapper;

import be.heh.stragram.adapter.in.web.dto.PostDtos;
import be.heh.stragram.application.domain.model.Favorite;
import be.heh.stragram.application.domain.model.Like;
import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.FavoritePostPort;
import be.heh.stragram.application.port.out.ImageStoragePort;
import be.heh.stragram.application.port.out.LikePostPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostWebMapper {

    private final LoadUserPort loadUserPort;
    private final LikePostPort likePostPort;
    private final FavoritePostPort favoritePostPort;
    private final ImageStoragePort imageStoragePort;

    public PostDtos.PostResponse toPostResponse(Post post, UserId currentUserId) {
        User author = loadUserPort.findById(post.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Post author not found"));

        boolean isLiked = false;
        boolean isFavorited = false;
        
        if (currentUserId != null) {
            isLiked = likePostPort.exists(post.getId(), currentUserId);
            isFavorited = favoritePostPort.exists(post.getId(), currentUserId);
        }

        return PostDtos.PostResponse.builder()
                .id(post.getId().toString())
                .authorId(author.getId().toString())
                .authorUsername(author.getUsername().toString())
                .authorAvatarUrl(author.getAvatarUrl())
                .imageUrl(imageStoragePort.getImageUrl(post.getImagePath()))
                .description(post.getDescription())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isLikedByCurrentUser(isLiked)
                .isFavoritedByCurrentUser(isFavorited)
                .build();
    }

    public PostDtos.LikeResponse toLikeResponse(Like like) {
        return PostDtos.LikeResponse.builder()
                .postId(like.getPostId().toString())
                .userId(like.getUserId().toString())
                .createdAt(like.getCreatedAt())
                .build();
    }

    public PostDtos.FavoriteResponse toFavoriteResponse(Favorite favorite) {
        return PostDtos.FavoriteResponse.builder()
                .postId(favorite.getPostId().toString())
                .userId(favorite.getUserId().toString())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
