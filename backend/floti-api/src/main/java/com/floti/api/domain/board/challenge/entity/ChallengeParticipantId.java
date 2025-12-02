package com.floti.api.domain.board.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 챌린지 참여자 테이블의 복합 기본 키를 표현하는 임베디드 ID 클래스이다.
 * challenge_id와 participant_id 두 컬럼을 묶어 엔티티의 식별자로 사용한다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChallengeParticipantId implements Serializable {
    /**
     * 챌린지 게시글 ID.
     */
    @Column(name = "challenge_id")
    private Long challengeId;

    /**
     * 사용자 ID (참여자 ID).
     */
    @Column(name = "participant_id")
    private Long participantId;
}