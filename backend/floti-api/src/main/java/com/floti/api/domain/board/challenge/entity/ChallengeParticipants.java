package com.floti.api.domain.board.challenge.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 각 챌린지와 사용자 간의 참여 관계를 저장하는 엔티티이다.
 * 복합 키(챌린지 ID + 사용자 ID)를 통해 한 사용자가 같은 챌린지에 두 번 이상 가입하지 못하도록 한다.
 * 진행률(progress)과 공헌도(contribution)를 함께 저장한다.
 */
@Entity
@Getter
@Table(name = "challenge_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeParticipants {
    /**
     * 복합 기본 키. challenge_id와 participant_id를 묶어서 사용한다.
     */
    @EmbeddedId
    private ChallengeParticipantId id;

    /**
     * 챌린지 게시글.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("challengeId")
    @JoinColumn(name = "challenge_id")
    private ChallengePosts challenge;

    /**
     * 참여자(사용자).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("participantId")
    @JoinColumn(name = "participant_id")
    private User participant;

    /**
     * 챌린지 진행률(0~100).
     */
    @Column(nullable = false)
    private int progress;

    /**
     * 공헌도(미션 완료 횟수 등). 비즈니스 요구 사항에 따라 사용된다.
     */
    @Column(nullable = false)
    private int contribution;

    /**
     * 빌더를 통해 엔티티를 생성한다. 새로 참여할 때 progress와 contribution은 0으로 초기화된다.
     */
    @Builder
    public ChallengeParticipants(ChallengePosts challenge, User participant) {
        this.challenge = challenge;
        this.participant = participant;
        this.id = new ChallengeParticipantId(challenge.getId(), participant.getId());
        this.progress = 0;
        this.contribution = 0;
    }

    /**
     * 진행률을 변경한다. 0~100 범위 내에서 변경해야 하며 검증은 서비스 레이어에서 수행한다.
     */
    public void updateProgress(int progress) {
        this.progress = progress;
    }

    /**
     * 공헌도를 수정한다.
     */
    public void updateContribution(int contribution) {
        this.contribution = contribution;
    }

    /**
     * 공헌도를 1 증가시킨다.
     */
    public void incrementContribution() {
        this.contribution++;
    }

    /**
     * 공헌도를 1 감소시킨다. 음수가 되지 않도록 한다.
     */
    public void decrementContribution() {
        if (this.contribution > 0) {
            this.contribution--;
        }
    }
}