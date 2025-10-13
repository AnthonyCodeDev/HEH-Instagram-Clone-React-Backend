package com.example.backendstragram.domain.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.backendstragram.domain.model.Post;
import com.example.backendstragram.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final Cloudinary cloudinary;
    private final Map<Long, Post> posts = new HashMap<>();
    private final Map<Long, Set<Long>> postLikes = new HashMap<>(); // postId -> userIds
    private final AtomicLong postIdGenerator = new AtomicLong(1);

    public Post createPost(User user, MultipartFile image, String caption) throws IOException {
        // Upload de l'image sur Cloudinary
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                image.getBytes(),
                ObjectUtils.asMap(
                        "folder", "stragram_posts",
                        "resource_type", "image"
                )
        );

        // Récupérer l'URL sécurisée de l'image
        String imageUrl = (String) uploadResult.get("secure_url");

        Long id = postIdGenerator.getAndIncrement();
        Post post = Post.builder()
                .id(id)
                .imageUrl(imageUrl)
                .caption(caption)
                .createdAt(LocalDateTime.now())
                .user(user)
                .likes(0)
                .build();

        posts.put(id, post);
        postLikes.put(id, new HashSet<>());
        return post;
    }

    public List<Post> getFeed() {
        return posts.values().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Post> getUserPosts(Long userId) {
        return posts.values().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public void likePost(Long postId, Long userId) {
        Set<Long> likes = postLikes.get(postId);
        if (likes != null && likes.add(userId)) {
            posts.get(postId).setLikes(likes.size());
        }
    }

    public void unlikePost(Long postId, Long userId) {
        Set<Long> likes = postLikes.get(postId);
        if (likes != null && likes.remove(userId)) {
            posts.get(postId).setLikes(likes.size());
        }
    }

    public boolean hasUserLiked(Long postId, Long userId) {
        Set<Long> likes = postLikes.get(postId);
        return likes != null && likes.contains(userId);
    }

    public Optional<Post> getPost(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }
}
