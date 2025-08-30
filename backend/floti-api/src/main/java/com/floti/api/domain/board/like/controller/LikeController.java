package com.floti.api.domain.board.like.controller;

import com.floti.api.domain.board.like.dto.LikeResponse;
import com.floti.api.domain.board.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/likes")
public class LikeController {
    private final LikeService likeService;

    /* 1. Tip 게시글 추천 토글 */
    @PostMapping("/tip/posts/{postId}")
    public ResponseEntity<LikeResponse> toggleLikeTipPost(@RequestParam Long userId, //임시
                                                          @PathVariable Long postId) {
        LikeResponse response = likeService.toggleLikeTipPost(userId, postId);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 2. 답변 추천 토글 */
    @PostMapping("/tip/comments/{answerId}")
    public ResponseEntity<LikeResponse> toggleLikeAnswer(@RequestParam Long userId, //임시
                                                         @PathVariable Long answerId) {
        LikeResponse response = likeService.toggleLikeAnswer(userId, answerId);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
