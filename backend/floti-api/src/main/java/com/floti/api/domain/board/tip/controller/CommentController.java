package com.floti.api.domain.board.tip.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.service.CommentService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/tips")
public class CommentController {
    private final CommentService commentService;
    private final AuthUtil authUtil;

    /* 1. 조회 */
    @GetMapping("/{postId}/comments")
    public List<CommentResponse> getComments(@PathVariable long postId) {
        return commentService.getComments(postId);
    }

    /* 2. 등록 */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long postId,
                                                         @Validated @RequestBody CommentRequest comment) {
        User user = authUtil.resolveUser(userDetails);
        CommentResponse response = commentService.createComment(user, postId, comment);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 3. 수정 */
    @PutMapping("/{postId}/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long postId, @PathVariable long id,
                                                         @Validated @RequestBody CommentRequest comment) {
        Long userId = authUtil.resolveUserId(userDetails);
        CommentResponse response = commentService.updateComment(userId, id, comment);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 4. 삭제 */
    @DeleteMapping("/{postId}/comments/{id}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable long postId, @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
