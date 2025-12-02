package com.floti.api.domain.board.challenge.repository;

import com.floti.api.domain.board.challenge.entity.ChallengeFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 챌린지 피드를 위한 레포지토리. 페이징된 조회를 제공한다.
 */
public interface ChallengeFeedRepository extends JpaRepository<ChallengeFeed, Long> {
    /**
     * 특정 챌린지에 속한 피드를 페이지 단위로 조회한다.
     *
     * @param challengeId 챌린지 ID
     * @param pageable 페이지 정보
     * @return 페이징된 피드 목록
     */
    Page<ChallengeFeed> findByChallengeId(Long challengeId, Pageable pageable);
}