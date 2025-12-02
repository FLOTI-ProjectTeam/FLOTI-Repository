package com.floti.api.domain.board.challenge.repository;

import com.floti.api.domain.board.challenge.entity.ChallengeParticipantId;
import com.floti.api.domain.board.challenge.entity.ChallengeParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 챌린지 참가자 정보를 위한 레포지토리. 복합 키를 사용하므로 ID 타입으로 ChallengeParticipantId를 지정한다.
 */
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipants, ChallengeParticipantId> {
    /**
     * 특정 사용자가 참여한 모든 챌린지를 조회한다.
     *
     * @param participantId 사용자 ID
     * @return 참가 정보 목록
     */
    List<ChallengeParticipants> findByParticipantId(Long participantId);

    /**
     * 특정 챌린지의 모든 참가자 정보를 조회한다.
     *
     * @param challengeId 챌린지 게시글 ID
     * @return 참가 정보 목록
     */
    List<ChallengeParticipants> findByChallengeId(Long challengeId);

    /**
     * 사용자가 이미 챌린지에 참여했는지 여부를 확인한다.
     *
     * @param challengeId 챌린지 ID
     * @param participantId 사용자 ID
     * @return true이면 이미 참가한 경우
     */
    boolean existsByChallengeIdAndParticipantId(Long challengeId, Long participantId);
}