package com.floti.api.domain.board.challenge.repository;

import com.floti.api.domain.board.challenge.entity.ChallengePosts;
import com.floti.api.domain.board.challenge.entity.QChallengeParticipants;
import com.floti.api.domain.board.challenge.entity.QChallengePosts;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QueryDSL을 이용한 챌린지 게시글 검색/정렬 구현체.
 * 목록 조회와 사용자 참여 목록 조회를 지원한다.
 */
@Repository
@RequiredArgsConstructor
public class CustomChallengePostRepositoryImpl implements CustomChallengePostRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChallengePosts> searchChallengePosts(String search, String sort, Pageable pageable) {
        QChallengePosts post = QChallengePosts.challengePosts;

        BooleanExpression condition = createSearchCondition(post, search);
        OrderSpecifier<?>[] orderSpecifiers = createSortSpecifiers(post, sort);

        Long total = queryFactory
                .select(post.id.count())
                .from(post)
                .where(condition)
                .fetchOne();
        long totalCount = (total != null) ? total : 0L;

        List<ChallengePosts> content = queryFactory
                .selectFrom(post)
                .where(condition)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<ChallengePosts> searchUserChallengePosts(Long userId, String search, String sort, Pageable pageable) {
        QChallengePosts post = QChallengePosts.challengePosts;
        QChallengeParticipants participant = QChallengeParticipants.challengeParticipants;

        BooleanExpression baseCondition = participant.participant.id.eq(userId)
                .and(participant.challenge.id.eq(post.id));
        // 검색 조건
        BooleanExpression searchCondition = createSearchCondition(post, search);
        BooleanExpression condition = baseCondition.and(searchCondition);

        OrderSpecifier<?>[] orderSpecifiers = createSortSpecifiers(post, sort);

        Long total = queryFactory
                .select(post.id.countDistinct())
                .from(post)
                .join(participant).on(participant.challenge.id.eq(post.id))
                .where(condition)
                .fetchOne();
        long totalCount = (total != null) ? total : 0L;

        List<ChallengePosts> content = queryFactory
                .select(post)
                .distinct()
                .from(post)
                .join(participant).on(participant.challenge.id.eq(post.id))
                .where(condition)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 검색 조건을 생성한다. 검색어가 비어 있으면 null을 반환하여 조건 없이 전체 조회한다.
     */
    private BooleanExpression createSearchCondition(QChallengePosts post, String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        String keyword = search.trim();
        return post.title.containsIgnoreCase(keyword)
                .or(post.intro.containsIgnoreCase(keyword))
                .or(post.content.containsIgnoreCase(keyword));
    }

    /**
     * 정렬 기준을 생성한다. participants는 현재 참가 인원 수 기준 내림차순,
     * latest는 최신 등록일 내림차순을 의미한다.
     */
    private OrderSpecifier<?>[] createSortSpecifiers(QChallengePosts post, String sort) {
        if (sort == null) sort = "";
        return switch (sort.toLowerCase()) {
            case "participants" -> new OrderSpecifier[]{post.currentParticipants.desc(), post.id.desc()};
            case "started" -> new OrderSpecifier[]{post.startDate.asc(), post.id.desc()};
            default -> new OrderSpecifier[]{post.createdAt.desc(), post.id.desc()};
        };
    }
}