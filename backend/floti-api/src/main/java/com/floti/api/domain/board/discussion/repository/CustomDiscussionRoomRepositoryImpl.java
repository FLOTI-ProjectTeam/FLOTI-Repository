package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import com.floti.api.domain.board.discussion.entity.QDiscussionRooms;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomDiscussionRoomRepositoryImpl implements CustomDiscussionRoomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DiscussionRooms> searchDiscussionRooms(String search, String sort, Pageable pageable) {
        QDiscussionRooms discussion = QDiscussionRooms.discussionRoom;

        // 1. 검색 조건
        BooleanExpression condition = discussion.title.containsIgnoreCase(search)
                .or(discussion.intro.containsIgnoreCase(search));

        // 2. 정렬 조건
        OrderSpecifier<Long> baseOrder = discussion.id.desc();
        OrderSpecifier<?>[] sortSpec = switch (sort.toLowerCase()) {
            case "accuracy" -> new OrderSpecifier[]{
                    new CaseBuilder()
                            .when(discussion.title.containsIgnoreCase(search))
                            .then(0)
                            .otherwise(1)
                            .asc(),
                    discussion.recentActivityAt.desc(), baseOrder
            };
            case "latest" -> new OrderSpecifier[] {baseOrder};
            default -> new OrderSpecifier[] {discussion.recentActivityAt.desc(), baseOrder};
        };

        // 3. 총 개수
        Long totalCount = queryFactory
                .select(discussion.count())
                .from(discussion)
                .where(condition)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0L;

        // 4. 실제 데이터
        List<DiscussionRooms> content = queryFactory.selectFrom(discussion)
                .where(condition)
                .orderBy(sortSpec)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
