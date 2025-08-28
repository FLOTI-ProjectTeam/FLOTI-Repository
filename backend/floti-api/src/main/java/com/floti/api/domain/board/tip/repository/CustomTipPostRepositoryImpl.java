package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.board.tip.entity.QTipPosts;
import com.floti.api.domain.board.tip.entity.TipPosts;
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
public class CustomTipPostRepositoryImpl implements CustomTipPostRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TipPosts> searchTipPosts(String search, String sort, Pageable pageable) {
        QTipPosts tip = QTipPosts.tipPost;

        /* 검색 조건 */
        BooleanExpression condition = tip.title.containsIgnoreCase(search)
                .or(tip.content.containsIgnoreCase(search));

        /* 정렬 조건 */
        OrderSpecifier<?>[] sortSpec = switch (sort.toLowerCase()) {
            case "accuracy" -> new OrderSpecifier[]{
                    new CaseBuilder()
                            .when(tip.title.containsIgnoreCase(search))
                            .then(0)
                            .otherwise(1)
                            .asc(),
                    tip.id.desc()
            };
            case "likes" -> new OrderSpecifier[] {tip.likeCount.desc()};
            default -> new OrderSpecifier[] {tip.id.desc()};
        };

        Long totalCount = queryFactory
                .select(tip.count())
                .from(tip)
                .where(condition)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0L;

        List<TipPosts> content = queryFactory.selectFrom(tip)
                .where(condition)
                .orderBy(sortSpec)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
