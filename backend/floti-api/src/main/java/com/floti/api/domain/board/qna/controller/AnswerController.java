package com.floti.api.domain.board.qna.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.qna.dto.AnswerRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.service.AnswerService;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/qnas")
public class AnswerController {
    private final AnswerService answerService;
    private final LikeService likeService;
    private final AuthUtil authUtil;

    /* 1. 등록 */
    @PostMapping("/{postId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                                       @Validated @RequestBody AnswerRequest answer,
                                                       @PathVariable long postId) {
        User user = authUtil.resolveUser(userDetails);
        AnswerResponse response = answerService.createAnswer(user, postId, answer);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 2. 수정 */
    @PutMapping("/{postId}/answers/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                                       @Validated @RequestBody AnswerRequest Answer,
                                                       @PathVariable long postId, @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        AnswerResponse response = answerService.updateAnswer(userId, id, Answer);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 3. 삭제 */
    @DeleteMapping("/{postId}/answers/{id}")
    public ResponseEntity<Void> deleteAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable long postId, @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        answerService.deleteAnswer(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /* 4. 채택 */
    @PatchMapping("/{postId}/answers/{id}/accept")
    public ResponseEntity<Void> acceptAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable long postId, @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        answerService.acceptAnswer(userId, postId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /* 5. 좋아요 토글 */
    @PostMapping("/{postId}/answers/{id}/like")
    public ResponseEntity<LikeResponse> toggleLikeAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long postId, @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        LikeResponse response = likeService.toggleLikeAnswer(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
