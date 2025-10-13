package be.heh.stragram.adapter.in.web;

import be.heh.stragram.adapter.in.web.dto.CommentDtos;
import be.heh.stragram.adapter.in.web.mapper.CommentWebMapper;
import be.heh.stragram.application.domain.model.Comment;
import be.heh.stragram.application.domain.value.CommentId;
import be.heh.stragram.application.domain.value.PostId;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.AddCommentUseCase;
import be.heh.stragram.application.port.in.DeleteCommentUseCase;
import be.heh.stragram.application.port.in.EditCommentUseCase;
import be.heh.stragram.application.port.in.ListPostCommentsQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final AddCommentUseCase addCommentUseCase;
    private final EditCommentUseCase editCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final ListPostCommentsQuery listPostCommentsQuery;
    private final CommentWebMapper commentWebMapper;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDtos.CommentResponse> addComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentDtos.AddCommentRequest request,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Comment comment = addCommentUseCase.add(
                PostId.fromString(postId),
                currentUserId,
                AddCommentUseCase.AddCommentCommand.builder()
                        .text(request.getText())
                        .build()
        );

        return new ResponseEntity<>(commentWebMapper.toCommentResponse(comment), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDtos.CommentListResponse> getPostComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<Comment> comments = listPostCommentsQuery.list(PostId.fromString(postId), page, size);
        
        List<CommentDtos.CommentResponse> commentResponses = comments.stream()
                .map(commentWebMapper::toCommentResponse)
                .collect(Collectors.toList());

        CommentDtos.CommentListResponse response = CommentDtos.CommentListResponse.builder()
                .comments(commentResponses)
                .page(page)
                .size(size)
                .hasMore(commentResponses.size() == size)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDtos.CommentResponse> editComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentDtos.EditCommentRequest request,
            @AuthenticationPrincipal UserId currentUserId) {
        
        Comment updatedComment = editCommentUseCase.edit(
                CommentId.fromString(commentId),
                currentUserId,
                EditCommentUseCase.EditCommentCommand.builder()
                        .text(request.getText())
                        .build()
        );

        return ResponseEntity.ok(commentWebMapper.toCommentResponse(updatedComment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String commentId,
            @AuthenticationPrincipal UserId currentUserId) {
        
        deleteCommentUseCase.delete(CommentId.fromString(commentId), currentUserId);
        return ResponseEntity.noContent().build();
    }
}
