package com.floti.api.domain.board.challenge.repository;

import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL을 활용한 챌린지 게시글 검색 및 정렬을 위한 커스텀 레포지토리 인터페이스.
 */
public interface CustomChallengePostRepository {
    /**
     * 검색어와 정렬 조건에 따라 챌린지 목록을 조회한다.
     *
     * @param search 검색 키워드 (null 또는 빈 문자열이면 전체 조회)
     * @param sort   정렬 기준: participants, latest 등
     * @param pageable 페이지 정보 (페이지 번호와 크기)
     * @return 페이징된 챌린지 엔티티 목록
     */
    Page<ChallengePosts> searchChallengePosts(String search, String sort, Pageable pageable);

    /**
     * 특정 사용자가 참여 중인 챌린지에서 검색/정렬을 수행한다.
     *
     * @param userId 참여자 ID
     * @param search 검색 키워드
     * @param sort   정렬 기준
     * @param pageable 페이지 정보
     * @return 페이징된 챌린지 엔티티 목록
     */
    Page<ChallengePosts> searchUserChallengePosts(Long userId, String search, String sort, Pageable pageable);
}