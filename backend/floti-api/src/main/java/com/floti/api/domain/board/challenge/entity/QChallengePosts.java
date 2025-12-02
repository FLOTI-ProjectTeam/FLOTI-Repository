package com.floti.api.domain.board.challenge.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.time.LocalDateTime;

/**
 * ChallengePosts 엔티티용 수동 Q 클래스.
 * QueryDSL을 통해 챌린지 게시글을 검색/정렬할 때 필요한 필드들을 노출한다.
 */
public class QChallengePosts extends EntityPathBase<ChallengePosts> {
    public static final QChallengePosts challengePosts = new QChallengePosts("challengePosts");

    /** 게시글 ID */
    public final NumberPath<Long> id = createNumber("id", Long.class);
    /** 제목 */
    public final StringPath title = createString("title");
    /** 한 줄 소개 */
    public final StringPath intro = createString("intro");
    /** 내용 */
    public final StringPath content = createString("content");
    /** 현재 참가 인원 */
    public final NumberPath<Integer> currentParticipants = createNumber("currentParticipants", Integer.class);
    /** 시작일 */
    public final DateTimePath<LocalDateTime> startDate = createDateTime("startDate", LocalDateTime.class);
    /** 작성 시각 */
    public final DateTimePath<LocalDateTime> createdAt = createDateTime("createdAt", LocalDateTime.class);

    public QChallengePosts(String variable) {
        super(ChallengePosts.class, PathMetadataFactory.forVariable(variable));
    }
}
