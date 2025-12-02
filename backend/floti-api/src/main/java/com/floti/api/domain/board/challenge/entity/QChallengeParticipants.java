package com.floti.api.domain.board.challenge.entity;

import com.floti.api.domain.auth.entity.QUser;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

/**
 * ChallengeParticipants 엔티티용 수동 Q 클래스.
 * 챌린지 참가자 정보에서 관계 엔티티의 속성을 탐색할 때 사용된다.
 */
public class QChallengeParticipants extends EntityPathBase<ChallengeParticipants> {
    public static final QChallengeParticipants challengeParticipants = new QChallengeParticipants("challengeParticipants");

    /** 참가자(User) 정보. User의 id를 참조하기 위해 QUser를 사용한다. */
    public final QUser participant = new QUser("participant");
    /** 챌린지 게시글. 챌린지의 id를 사용하기 위해 QChallengePosts를 사용한다. */
    public final QChallengePosts challenge = new QChallengePosts("challenge");
    /** 진행률 */
    public final NumberPath<Integer> progress = createNumber("progress", Integer.class);
    /** 공헌도 */
    public final NumberPath<Integer> contribution = createNumber("contribution", Integer.class);

    public QChallengeParticipants(String variable) {
        super(ChallengeParticipants.class, PathMetadataFactory.forVariable(variable));
    }
}
