package com.floti.api.domain.board.discussion.controller;

import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/discussions")
public class MessageController {
    private final LikeService likeService;
    private final AuthUtil authUtil;

    /* 추천 토글 */
    @PostMapping("/messages/{id}")
    public ResponseEntity<LikeResponse> toggleLikeMessage(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        LikeResponse response = likeService.toggleLikeMessage(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
