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
    @PostMapping("/tip/posts/{id}")
    public ResponseEntity<LikeResponse> toggleLikeTipPost(@RequestParam Long userId, //임시
                                                          @PathVariable Long id) {
        LikeResponse response = likeService.toggleLikeTipPost(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
