package com.floti.api.domain.board.discussion.repository;

import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import com.floti.api.domain.board.discussion.entity.QDiscussionPosts;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomDiscussionPostRepositoryImpl implements CustomDiscussionPostRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DiscussionPosts> searchDiscussionPosts(String search, String sort, Pageable pageable) {
        QDiscussionPosts discussion = QDiscussionPosts.discussionPost;

        // 1. 검색 조건
        BooleanExpression condition = discussion.title.containsIgnoreCase(search)
                .or(discussion.intro.containsIgnoreCase(search));

        // 2. 정렬 조건
        OrderSpecifier<?>[] sortSpec = switch (sort.toLowerCase()) {
            case "accuracy" -> new OrderSpecifier[]{
                    new CaseBuilder()
                            .when(discussion.title.containsIgnoreCase(search))
                            .then(0)
                            .otherwise(1)
                            .asc(),
                    discussion.id.desc()
            };
            default -> new OrderSpecifier[] {discussion.id.desc()};
        };

        // 3. 총 개수
        Long totalCount = queryFactory
                .select(discussion.count())
                .from(discussion)
                .where(condition)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0L;

        // 4. 실제 데이터
        List<DiscussionPosts> content = queryFactory.selectFrom(discussion)
                .where(condition)
                .orderBy(sortSpec)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
