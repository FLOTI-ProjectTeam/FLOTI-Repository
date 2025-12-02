package com.floti.api.domain.board.challenge.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.challenge.dto.ChallengeDetailResponse;
import com.floti.api.domain.board.challenge.dto.FeedRequest;
import com.floti.api.domain.board.challenge.entity.ChallengeFeed;
import com.floti.api.domain.board.challenge.entity.ChallengeParticipants;
import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import com.floti.api.domain.board.challenge.repository.ChallengeFeedRepository;
import com.floti.api.domain.board.challenge.repository.ChallengeParticipantRepository;
import com.floti.api.domain.board.challenge.repository.ChallengePostRepository;
import com.floti.api.error.ExceptionMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ChallengeService의 비즈니스 로직을 검증하는 단위 테스트.
 * Repository를 목 객체로 주입하여 서비스 레이어의 동작을 분리해서 테스트한다.
 */
@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {

    @InjectMocks
    private ChallengeService challengeService;

    @Mock
    private ChallengePostRepository challengePostRepository;

    @Mock
    private ChallengeParticipantRepository challengeParticipantRepository;

    @Mock
    private ChallengeFeedRepository challengeFeedRepository;

    @Test
    @DisplayName("getChallengeDetail: 전체 진행률과 개인 진행률 계산")
    void getChallengeDetail_returnsProgressAndMyProgress() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User user2 = User.builder().id(2L).nickname("참여자2").build();
        // 게시글은 아직 종료되지 않은 상태
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        // 참여자 리스트: 작성자(progress 50), user2(progress 100)
        ChallengeParticipants cp1 = ChallengeParticipants.builder()
                .challenge(post)
                .participant(author)
                .build();
        cp1.updateProgress(50);
        ChallengeParticipants cp2 = ChallengeParticipants.builder()
                .challenge(post)
                .participant(user2)
                .build();
        cp2.updateProgress(100);

        when(challengePostRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(challengeParticipantRepository.findByChallengeId(anyLong()))
                .thenReturn(List.of(cp1, cp2));

        // when: 작성자가 상세 조회
        ChallengeDetailResponse response = challengeService.getChallengeDetail(author.getId(), 1L);

        // then
        assertEquals(2, response.participants().size());
        assertEquals(75.0, response.progress()); // 전체 평균 진행률 (50 + 100) / 2
        assertEquals(50, response.myProgress()); // 작성자는 본인 진행률 50

        // when: 두 번째 사용자가 상세 조회
        ChallengeDetailResponse response2 = challengeService.getChallengeDetail(user2.getId(), 1L);
        assertEquals(100, response2.myProgress());
    }

    @Test
    @DisplayName("joinChallenge: 정상 참여 - 참가자 저장 및 인원 증가")
    void joinChallenge_success() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User joiner = User.builder().id(2L).nickname("참여자").build();
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(2)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        when(challengePostRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(challengeParticipantRepository.existsByChallengeIdAndParticipantId(anyLong(), anyLong()))
                .thenReturn(false);

        // when
        challengeService.joinChallenge(joiner, 1L);

        // then
        verify(challengeParticipantRepository).save(any(ChallengeParticipants.class));
        verify(challengePostRepository).save(post);
        assertEquals(2, post.getCurrentParticipants());
    }

    @Test
    @DisplayName("joinChallenge: 이미 참여한 경우 예외")
    void joinChallenge_alreadyJoined() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User joiner = User.builder().id(2L).nickname("참여자").build();
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(3)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        when(challengePostRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(challengeParticipantRepository.existsByChallengeIdAndParticipantId(anyLong(), anyLong()))
                .thenReturn(true);

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> challengeService.joinChallenge(joiner, 1L));
        assertEquals(ExceptionMessage.JOIN_DENIED, ex.getMessage());
    }

    @Test
    @DisplayName("joinChallenge: 정원 초과일 때 예외")
    void joinChallenge_full() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User joiner = User.builder().id(2L).nickname("참여자").build();
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(1)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        when(challengePostRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(challengeParticipantRepository.existsByChallengeIdAndParticipantId(anyLong(), anyLong()))
                .thenReturn(false);

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> challengeService.joinChallenge(joiner, 1L));
        assertEquals(ExceptionMessage.CHALLENGE_FULL, ex.getMessage());
    }

    @Test
    @DisplayName("createFeed: 참가자가 아닌 경우 AccessDeniedException")
    void createFeed_notParticipant() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User outsider = User.builder().id(2L).nickname("외부인").build();
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(3)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        when(challengePostRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(challengeParticipantRepository.existsByChallengeIdAndParticipantId(anyLong(), anyLong()))
                .thenReturn(false);

        FeedRequest request = new FeedRequest();
        request.setContent("성과 공유");

        // when & then
        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> challengeService.createFeed(outsider, 1L, request));
        assertEquals(ExceptionMessage.NOT_PARTICIPANT, ex.getMessage());
    }

    @Test
    @DisplayName("updateFeed: 작성자가 아닌 경우 AccessDeniedException")
    void updateFeed_notAuthor() {
        // given
        User author = User.builder().id(1L).nickname("작성자").build();
        User other = User.builder().id(2L).nickname("다른사람").build();
        ChallengePosts post = ChallengePosts.builder()
                .author(author)
                .title("챌린지")
                .intro("소개")
                .content("내용")
                .maxParticipants(3)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        ChallengeFeed feed = ChallengeFeed.builder()
                .challenge(post)
                .author(author)
                .content("기존 내용")
                .build();
        when(challengeFeedRepository.findById(anyLong()))
                .thenReturn(Optional.of(feed));

        FeedRequest request = new FeedRequest();
        request.setContent("수정된 내용");

        // when & then
        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> challengeService.updateFeed(other.getId(), 1L, request));
        assertEquals(ExceptionMessage.UPDATE_DENIED, ex.getMessage());
    }
}