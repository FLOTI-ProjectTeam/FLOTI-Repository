package com.floti.api.domain.board.challenge.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.challenge.dto.*;
import com.floti.api.domain.board.challenge.entity.ChallengeFeed;
import com.floti.api.domain.board.challenge.entity.ChallengeParticipantId;
import com.floti.api.domain.board.challenge.entity.ChallengeParticipants;
import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import com.floti.api.domain.board.challenge.repository.ChallengeFeedRepository;
import com.floti.api.domain.board.challenge.repository.ChallengeParticipantRepository;
import com.floti.api.domain.board.challenge.repository.ChallengePostRepository;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 챌린지 게시판 도메인의 비즈니스 로직을 담당하는 서비스.
 * 게시글 CRUD, 참가/탈퇴, 진행률 계산, 피드 CRUD 등을 수행한다.
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengePostRepository challengePostRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeFeedRepository challengeFeedRepository;

    /**
     * 한 페이지에 보여줄 챌린지 수. 기존 게시판과 동일하게 20개로 고정한다.
     */
    private static final int PAGE_SIZE = 20;

    /**
     * 만료된 챌린지인지 확인하고 종료되었다면 완료 처리한다.
     * 
     * @param post 챌린지 엔티티
     */
    private void updateCompletionStatus(ChallengePosts post) {
        if (!post.isCompleted() && LocalDateTime.now().isAfter(post.getEndDate())) {
            post.complete();
            // isCompleted 값이 변경되었으므로 즉시 저장
            challengePostRepository.save(post);
        }
    }

    /**
     * 전체 챌린지 목록을 조회한다. 검색어가 비어 있으면 전체 목록을, 그렇지 않으면 검색 결과를 반환한다.
     * 
     * @param search 검색어 (nullable)
     * @param sort   정렬 기준
     * @param page   페이지 번호 (0부터 시작)
     */
    /**
     * 전체 챌린지 목록을 조회한다. 검색어가 비어 있으면 전체 목록을, 그렇지 않으면 검색 결과를 반환한다.
     * N+1 문제를 해결하기 위해 진행률을 Bulk 연산으로 한 번에 조회하여 매핑한다.
     * 
     * @param search 검색어 (nullable)
     * @param sort   정렬 기준
     * @param page   페이지 번호 (0부터 시작)
     */
    public Page<ChallengeSummaryResponse> getChallenges(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<ChallengePosts> challengePage = challengePostRepository.searchChallengePosts(search, sort, pageable);

        // Bulk 연산: 조회된 챌린지 ID 목록 추출
        List<Long> ids = challengePage.getContent().stream().map(ChallengePosts::getId).toList();

        // 메모리 매핑: [챌린지ID, 진행률] 통계 조회 후 Map으로 변환
        java.util.Map<Long, Double> progressMap = getProgressMap(ids);

        // DTO 변환 시 미리 계산된 진행률 주입
        return challengePage
                .map(post -> new ChallengeSummaryResponse(post, progressMap.getOrDefault(post.getId(), 0.0)));
    }

    /**
     * 특정 사용자가 참여 중인 챌린지 목록을 검색한다.
     * 
     * @param userId 사용자 ID
     * @param search 검색어
     * @param sort   정렬 기준
     * @param page   페이지 번호
     */
    public Page<ChallengeSummaryResponse> getUserChallenges(Long userId, String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<ChallengePosts> challengePage = challengePostRepository.searchUserChallengePosts(userId, search, sort,
                pageable);

        List<Long> ids = challengePage.getContent().stream().map(ChallengePosts::getId).toList();
        java.util.Map<Long, Double> progressMap = getProgressMap(ids);

        return challengePage
                .map(post -> new ChallengeSummaryResponse(post, progressMap.getOrDefault(post.getId(), 0.0)));
    }

    /**
     * 챌린지 ID 목록으로 진행률 통계를 Bulk 조회하여 Map으로 반환한다.
     */
    private java.util.Map<Long, Double> getProgressMap(List<Long> ids) {
        if (ids.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<Object[]> stats = challengeParticipantRepository.findProgressStatsByChallengeIds(ids);
        java.util.Map<Long, Double> map = new java.util.HashMap<>();
        for (Object[] row : stats) {
            Long id = (Long) row[0];
            Double avg = (Double) row[1];
            map.put(id, avg != null ? avg : 0.0);
        }
        return map;
    }

    /**
     * 챌린지 상세 정보를 조회한다. 참가자 목록과 전체 진행률, 현재 로그인한 사용자의 진행률을 함께 제공한다.
     * 
     * @param userId      로그인한 사용자 ID (로그인하지 않은 경우 null)
     * @param challengeId 조회할 챌린지 ID
     * @return 상세 응답 DTO
     */
    public ChallengeDetailResponse getChallengeDetail(Long userId, Long challengeId) {
        ChallengePosts post = challengePostRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CHALLENGE_NOT_FOUND));
        // 기간이 끝났으면 완료 처리
        updateCompletionStatus(post);

        List<ChallengeParticipants> participants = challengeParticipantRepository.findByChallengeId(challengeId);
        List<ParticipantResponse> responses = new ArrayList<>();
        int sumProgress = 0;
        Integer myProgress = null;

        for (ChallengeParticipants cp : participants) {
            User member = cp.getParticipant();
            int progress = cp.getProgress();
            sumProgress += progress;
            if (userId != null && member.getId().equals(userId)) {
                myProgress = progress;
            }
            responses.add(new ParticipantResponse(member, progress, cp.getContribution()));
        }

        double progress = participants.isEmpty() ? 0.0 : ((double) sumProgress) / participants.size();

        return new ChallengeDetailResponse(post, responses, progress, myProgress);
    }

    /**
     * 챌린지를 생성한다. 작성자는 자동으로 참여자로 등록된다.
     * 
     * @param user    작성자
     * @param request 요청 정보
     */
    @Transactional
    public ChallengeSummaryResponse createChallenge(User user, ChallengeRequest request) {
        // 엔티티 생성
        ChallengePosts post = ChallengePosts.builder()
                .author(user)
                .title(request.getTitle())
                .intro(request.getIntro())
                .content(request.getContent())
                .maxParticipants(request.getMaxParticipants())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        // 저장
        ChallengePosts saved = challengePostRepository.save(post);
        // 작성자를 참여자로 등록
        ChallengeParticipants cp = ChallengeParticipants.builder()
                .challenge(saved)
                .participant(user)
                .build();
        challengeParticipantRepository.save(cp);
        return new ChallengeSummaryResponse(saved);
    }

    /**
     * 챌린지 정보를 수정한다. 작성자만 수정할 수 있으며, 현재 참가 인원보다 작은 최대 인원으로는 수정할 수 없다.
     * 
     * @param userId  요청한 사용자 ID
     * @param id      수정할 챌린지 ID
     * @param request 수정 데이터
     */
    @Transactional
    public ChallengeSummaryResponse updateChallenge(Long userId, Long id, ChallengeRequest request) {
        ChallengePosts post = challengePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CHALLENGE_NOT_FOUND));
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);
        }
        // 최대 인원 수 체크
        if (request.getMaxParticipants() < post.getCurrentParticipants()) {
            throw new IllegalStateException(ExceptionMessage.CHALLENGE_FULL);
        }
        post.update(request.getTitle(), request.getIntro(), request.getContent(),
                request.getMaxParticipants(), request.getStartDate(), request.getEndDate());
        return new ChallengeSummaryResponse(post);
    }

    /**
     * 챌린지를 삭제한다. 작성자만 삭제할 수 있다.
     * 
     * @param userId 요청 사용자 ID
     * @param id     챌린지 ID
     */
    @Transactional
    public void deleteChallenge(Long userId, Long id) {
        ChallengePosts post = challengePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CHALLENGE_NOT_FOUND));
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);
        }
        challengePostRepository.delete(post);
    }

    /**
     * 사용자가 챌린지에 참여한다. 이미 참여 중이거나 정원이 찬 경우 예외를 발생시킨다.
     * 
     * @param user        참가자
     * @param challengeId 챌린지 ID
     */
    @Transactional
    public void joinChallenge(User user, Long challengeId) {
        ChallengePosts post = challengePostRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CHALLENGE_NOT_FOUND));
        updateCompletionStatus(post);
        if (post.isCompleted()) {
            throw new IllegalStateException(ExceptionMessage.JOIN_DENIED);
        }
        if (challengeParticipantRepository.existsByChallengeIdAndParticipantId(challengeId, user.getId())) {
            throw new IllegalStateException(ExceptionMessage.JOIN_DENIED);
        }
        if (post.getCurrentParticipants() >= post.getMaxParticipants()) {
            throw new IllegalStateException(ExceptionMessage.CHALLENGE_FULL);
        }
        // 참여자 추가
        ChallengeParticipants participant = ChallengeParticipants.builder()
                .challenge(post)
                .participant(user)
                .build();
        challengeParticipantRepository.save(participant);
        post.incrementParticipants();
        // 저장
        challengePostRepository.save(post);
    }

    /**
     * 챌린지 전체 진행률을 계산한다. 모든 참가자의 평균 진행률을 반환한다.
     * 
     * @param challengeId 챌린지 ID
     */
    public double calculateProgress(Long challengeId) {
        List<ChallengeParticipants> list = challengeParticipantRepository.findByChallengeId(challengeId);
        if (list.isEmpty())
            return 0.0;
        int sum = 0;
        for (ChallengeParticipants cp : list) {
            sum += cp.getProgress();
        }
        return ((double) sum) / list.size();
    }

    /**
     * 특정 사용자의 챌린지 진행률을 반환한다.
     * 
     * @param userId      사용자 ID
     * @param challengeId 챌린지 ID
     */
    public Integer getUserProgress(Long userId, Long challengeId) {
        ChallengeParticipantId id = new ChallengeParticipantId(challengeId, userId);
        Optional<ChallengeParticipants> optional = challengeParticipantRepository.findById(id);
        return optional.map(ChallengeParticipants::getProgress).orElse(null);
    }

    /**
     * 챌린지에 속한 피드 목록을 조회한다.
     * 
     * @param challengeId 챌린지 ID
     * @param page        페이지 번호
     */
    public Page<FeedResponse> getFeeds(Long challengeId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<ChallengeFeed> feedPage = challengeFeedRepository.findByChallengeId(challengeId, pageable);
        return feedPage.map(FeedResponse::new);
    }

    /**
     * 챌린지의 성과 공유 글을 작성한다. 참가자만 작성할 수 있다.
     * 
     * @param user        작성자
     * @param challengeId 챌린지 ID
     * @param request     내용
     */
    @Transactional
    public FeedResponse createFeed(User user, Long challengeId, FeedRequest request) {
        ChallengePosts post = challengePostRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CHALLENGE_NOT_FOUND));
        // 참가 여부 확인
        if (!challengeParticipantRepository.existsByChallengeIdAndParticipantId(challengeId, user.getId())) {
            throw new AccessDeniedException(ExceptionMessage.NOT_PARTICIPANT);
        }
        ChallengeFeed seed = ChallengeFeed.builder()
                .challenge(post)
                .author(user)
                .content(request.getContent())
                .build();
        ChallengeFeed saved = challengeFeedRepository.save(seed);

        // 공헌도 및 진행률 업데이트
        updateParticipantProgress(challengeId, user.getId(), post, true);

        return new FeedResponse(saved);
    }

    /**
     * 특정 피드를 조회한다.
     * 
     * @param feedId 피드 ID
     */
    public FeedResponse getFeed(Long feedId) {
        ChallengeFeed feed = challengeFeedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.FEED_NOT_FOUND));
        return new FeedResponse(feed);
    }

    /**
     * 챌린지 피드를 수정한다. 작성자 본인만 수정할 수 있다.
     * 
     * @param userId  사용자 ID
     * @param feedId  피드 ID
     * @param request 수정 내용
     */
    @Transactional
    public FeedResponse updateFeed(Long userId, Long feedId, FeedRequest request) {
        ChallengeFeed feed = challengeFeedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.FEED_NOT_FOUND));
        if (!feed.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);
        }
        feed.updateContent(request.getContent());
        return new FeedResponse(feed);
    }

    /**
     * 챌린지 피드를 삭제한다. 작성자 본인만 삭제할 수 있다.
     * 
     * @param userId 사용자 ID
     * @param feedId 피드 ID
     */
    @Transactional
    public void deleteFeed(Long userId, Long feedId) {
        ChallengeFeed feed = challengeFeedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.FEED_NOT_FOUND));
        if (!feed.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);
        }
        challengeFeedRepository.delete(feed);

        // 공헌도 및 진행률 업데이트 (감소)
        ChallengePosts post = feed.getChallenge();
        updateParticipantProgress(post.getId(), userId, post, false);
    }

    private void updateParticipantProgress(Long challengeId, Long userId, ChallengePosts post, boolean isIncrement) {
        ChallengeParticipantId pid = new ChallengeParticipantId(challengeId, userId);
        ChallengeParticipants participant = challengeParticipantRepository.findById(pid)
                .orElseThrow(() -> new AccessDeniedException(ExceptionMessage.NOT_PARTICIPANT));

        if (isIncrement) {
            participant.incrementContribution();
        } else {
            participant.decrementContribution();
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(post.getStartDate(), post.getEndDate()) + 1;
        if (totalDays <= 0)
            totalDays = 1; // 방어 로직

        int progress = (int) Math.min(100, (participant.getContribution() * 100.0 / totalDays));
        participant.updateProgress(progress);

        challengeParticipantRepository.save(participant);
    }
}