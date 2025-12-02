package com.floti.api.domain.board.challenge.repository;

import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 챌린지 게시글에 대한 기본 CRUD 및 커스텀 쿼리를 제공하는 레포지토리.
 */
public interface ChallengePostRepository extends JpaRepository<ChallengePosts, Long>, CustomChallengePostRepository {
    // 추가적인 메소드가 필요하다면 여기에 선언한다.
}