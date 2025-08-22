package com.floti.api.domain.board.tip.controller;

import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/tip")
public class CommentController {
    private final CommentService commentService;

    /* 1. 조회 */
    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    /* 2. 등록 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@Validated @RequestBody CommentRequest comment,
                                                         @PathVariable Long postId) {
        CommentResponse response = commentService.createComment(comment.getAuthorId(), postId, comment);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 3. 수정 */
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@Validated @RequestBody CommentRequest comment,
                                                         @PathVariable Long id) {
        CommentResponse response = commentService.updateComment(comment.getAuthorId(), id, comment);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 4. 삭제 */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@RequestParam Long userId, //임시
                                              @PathVariable Long id) {
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
