package com.floti.api.domain.board.challenge.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.challenge.entity.ChallengeParticipants;
import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChallengePostRepository에 대한 단위 테스트.
 * QueryDSL 검색/정렬 기능을 검증하기 위해 DataJpaTest 환경에서 실제 DB를 사용한다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
public class ChallengePostRepositoryTest {
    @Autowired
    private ChallengePostRepository challengePostRepository;

    @Autowired
    private ChallengeParticipantRepository challengeParticipantRepository;

    @Autowired
    private EntityManager em;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .email("user1@example.com")
                .username("user1")
                .password("password")
                .nickname("유저1")
                .build();
        em.persist(user1);

        user2 = User.builder()
                .email("user2@example.com")
                .username("user2")
                .password("password")
                .nickname("유저2")
                .build();
        em.persist(user2);

        // flush to ensure IDs are assigned before tests run
        em.flush();
    }

    @Test
    @DisplayName("searchChallengePosts: 참가자 수 기준 내림차순 정렬")
    void searchChallengePosts_sortByParticipants() {
        // given: 두 개의 게시글을 생성하고 참가자 수를 다르게 만든다.
        ChallengePosts post1 = ChallengePosts.builder()
                .author(user1)
                .title("검색어 포함 제목1")
                .intro("소개1")
                .content("내용1")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        ChallengePosts post2 = ChallengePosts.builder()
                .author(user1)
                .title("검색어 포함 제목2")
                .intro("소개2")
                .content("내용2")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        challengePostRepository.save(post1);
        challengePostRepository.save(post2);

        // post2는 추가 참가자 한 명이 더 있어야 한다.
        post2.incrementParticipants(); // currentParticipants = 2
        challengePostRepository.save(post2);

        // when: 참가자 수 기준으로 정렬
        Page<ChallengePosts> page = challengePostRepository.searchChallengePosts(
                "검색어", "participants", PageRequest.of(0, 20));

        // then: 총 2개이며, 참가자 수가 더 많은 post2가 먼저 나와야 한다.
        assertEquals(2, page.getTotalElements());
        assertEquals(post2.getId(), page.getContent().get(0).getId());
        assertEquals(post1.getId(), page.getContent().get(1).getId());
    }

    @Test
    @DisplayName("searchUserChallengePosts: 특정 사용자가 참여한 챌린지만 반환")
    void searchUserChallengePosts_returnsOnlyJoined() {
        // given: 세 개의 챌린지 게시글 생성
        ChallengePosts p1 = ChallengePosts.builder()
                .author(user1)
                .title("챌린지1")
                .intro("intro1")
                .content("content1")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        ChallengePosts p2 = ChallengePosts.builder()
                .author(user1)
                .title("챌린지2")
                .intro("intro2")
                .content("content2")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        ChallengePosts p3 = ChallengePosts.builder()
                .author(user1)
                .title("챌린지3")
                .intro("intro3")
                .content("content3")
                .maxParticipants(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        challengePostRepository.saveAll(List.of(p1, p2, p3));

        // user2가 p1과 p2에 참여
        ChallengeParticipants cp1 = ChallengeParticipants.builder()
                .challenge(p1)
                .participant(user2)
                .build();
        ChallengeParticipants cp2 = ChallengeParticipants.builder()
                .challenge(p2)
                .participant(user2)
                .build();
        challengeParticipantRepository.saveAll(List.of(cp1, cp2));

        // when: user2의 참여 챌린지를 조회
        Page<ChallengePosts> page = challengePostRepository.searchUserChallengePosts(
                user2.getId(), "", "participants", PageRequest.of(0, 20));

        // then: user2가 참여한 두 게시글만 반환되어야 한다.
        assertEquals(2, page.getTotalElements());
        List<Long> returnedIds = page.map(ChallengePosts::getId).toList();
        assertTrue(returnedIds.contains(p1.getId()));
        assertTrue(returnedIds.contains(p2.getId()));
        assertFalse(returnedIds.contains(p3.getId()));
    }
}