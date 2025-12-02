package com.floti.api.domain.board.challenge.controller;

import com.floti.api.domain.board.challenge.dto.FeedRequest;
import com.floti.api.domain.board.challenge.dto.FeedResponse;
import com.floti.api.domain.board.challenge.service.ChallengeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 챌린지 피드 개별 조회/수정/삭제를 담당하는 컨트롤러.
 * 목록 조회 및 작성은 ChallengeController에서 처리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/community/challenges/feeds")
public class ChallengeFeedController {
    private final ChallengeService challengeService;
    private final AuthUtil authUtil;

    /**
     * 피드 상세 정보를 조회한다.
     * @param id 피드 ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedResponse> getFeed(@PathVariable long id) {
        FeedResponse response = challengeService.getFeed(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 피드를 수정한다. 작성자만 수정 가능하다.
     * @param userDetails 인증된 사용자
     * @param id 피드 ID
     * @param request 수정 내용
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeedResponse> updateFeed(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable long id,
                                                   @Validated @RequestBody FeedRequest request) {
        Long userId = authUtil.resolveUserId(userDetails);
        FeedResponse response = challengeService.updateFeed(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 피드를 삭제한다. 작성자만 삭제 가능하다.
     * @param userDetails 인증된 사용자
     * @param id 피드 ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeed(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        challengeService.deleteFeed(userId, id);
        return ResponseEntity.noContent().build();
    }
}