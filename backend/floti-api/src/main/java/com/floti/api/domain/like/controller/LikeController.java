package com.floti.api.domain.like.controller;

import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/likes")
public class LikeController {
    private final LikeService likeService;
    private final AuthUtil authUtil;

    /* 1. Tip 게시글 추천 토글 */
    @PostMapping("/tip/posts/{postId}")
    public ResponseEntity<LikeResponse> toggleLikeTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable long postId) {
        Long userId = authUtil.resolveUserId(userDetails);
        LikeResponse response = likeService.toggleLikeTipPost(userId, postId);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 2. 답변 추천 토글 */
    @PostMapping("/tip/comments/{answerId}")
    public ResponseEntity<LikeResponse> toggleLikeAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long answerId) {
        Long userId = authUtil.resolveUserId(userDetails);
        LikeResponse response = likeService.toggleLikeAnswer(userId, answerId);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
