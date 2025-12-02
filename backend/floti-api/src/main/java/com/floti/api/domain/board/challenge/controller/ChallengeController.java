package com.floti.api.domain.board.challenge.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.challenge.dto.*;
import com.floti.api.domain.board.challenge.service.ChallengeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * 챌린지 게시판과 관련된 HTTP 요청을 처리하는 컨트롤러.
 * 목록 조회, 상세 조회, 생성/수정/삭제, 참여, 진행률, 피드 작성/조회 등을 담당한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/community/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;
    private final AuthUtil authUtil;

    /**
     * 전체 챌린지 목록을 조회한다.
     * @param search 검색 키워드 (옵션)
     * @param sort   정렬 기준: participants, latest, started
     * @param page   페이지 번호
     * @return 챌린지 목록 페이지
     */
    @GetMapping
    public Page<ChallengeSummaryResponse> getChallenges(@RequestParam(required = false) String search,
                                                       @RequestParam(defaultValue = "latest") String sort,
                                                       @RequestParam(defaultValue = "0") int page) {
        return challengeService.getChallenges(search, sort, page);
    }

    /**
     * 로그인한 사용자가 참여 중인 챌린지 목록을 조회한다.
     * @param userDetails 인증된 사용자 정보
     * @param search 검색 키워드 (옵션)
     * @param sort   정렬 기준
     * @param page   페이지 번호
     * @return 참가 중인 챌린지 목록 페이지
     */
    @GetMapping("/mine")
    public Page<ChallengeSummaryResponse> getMyChallenges(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam(required = false) String search,
                                                          @RequestParam(defaultValue = "latest") String sort,
                                                          @RequestParam(defaultValue = "0") int page) {
        Long userId = authUtil.resolveUserId(userDetails);
        return challengeService.getUserChallenges(userId, search, sort, page);
    }

    /**
     * 챌린지 상세 정보를 조회한다.
     * @param userDetails 로그인한 사용자 정보 (없어도 가능)
     * @param id 챌린지 ID
     * @return 상세 정보와 진행률
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDetailResponse> getChallenge(@AuthenticationPrincipal UserDetails userDetails,
                                                                @PathVariable long id) {
        Long userId = (userDetails != null) ? authUtil.resolveUserId(userDetails) : null;
        ChallengeDetailResponse response = challengeService.getChallengeDetail(userId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 챌린지를 등록한다. 작성자는 자동으로 참여자로 등록된다.
     * @param userDetails 인증된 사용자
     * @param request 챌린지 생성 요청
     */
    @PostMapping
    public ResponseEntity<ChallengeSummaryResponse> createChallenge(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @Validated @RequestBody ChallengeRequest request) {
        User user = authUtil.resolveUser(userDetails);
        ChallengeSummaryResponse response = challengeService.createChallenge(user, request);
        return ResponseEntity.status(CREATED).body(response);
    }

    /**
     * 챌린지를 수정한다. 작성자만 수정할 수 있다.
     * @param userDetails 인증된 사용자
     * @param id 챌린지 ID
     * @param request 수정 요청
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeSummaryResponse> updateChallenge(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable long id,
                                                                   @Validated @RequestBody ChallengeRequest request) {
        Long userId = authUtil.resolveUserId(userDetails);
        ChallengeSummaryResponse response = challengeService.updateChallenge(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 챌린지를 삭제한다. 작성자만 삭제할 수 있다.
     * @param userDetails 인증된 사용자
     * @param id 챌린지 ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        challengeService.deleteChallenge(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자가 챌린지에 참여한다. 성공 시 본문 없는 200 OK를 반환한다.
     * @param userDetails 인증된 사용자
     * @param id 챌린지 ID
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinChallenge(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable long id) {
        User user = authUtil.resolveUser(userDetails);
        challengeService.joinChallenge(user, id);
        return ResponseEntity.ok().build();
    }

    /**
     * 챌린지 진행률을 조회한다. 로그인한 사용자는 자신의 진행률도 함께 확인할 수 있다.
     * @param userDetails 인증된 사용자 정보 (nullable)
     * @param id 챌린지 ID
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getProgress(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable long id) {
        double progress = challengeService.calculateProgress(id);
        Map<String, Object> body = new HashMap<>();
        body.put("progress", progress);
        if (userDetails != null) {
            Long userId = authUtil.resolveUserId(userDetails);
            Integer my = challengeService.getUserProgress(userId, id);
            body.put("myProgress", my);
        }
        return ResponseEntity.ok(body);
    }

    /**
     * 챌린지에 속한 피드 목록을 조회한다.
     * @param id 챌린지 ID
     * @param page 페이지 번호
     */
    @GetMapping("/{id}/feeds")
    public Page<FeedResponse> getFeeds(@PathVariable long id,
                                       @RequestParam(defaultValue = "0") int page) {
        return challengeService.getFeeds(id, page);
    }

    /**
     * 챌린지 성과 공유 글을 작성한다. 참가자만 작성할 수 있다.
     * @param userDetails 인증된 사용자
     * @param id 챌린지 ID
     * @param request 내용
     */
    @PostMapping("/{id}/feeds")
    public ResponseEntity<FeedResponse> createFeed(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable long id,
                                                   @Validated @RequestBody FeedRequest request) {
        User user = authUtil.resolveUser(userDetails);
        FeedResponse response = challengeService.createFeed(user, id, request);
        return ResponseEntity.status(CREATED).body(response);
    }
}