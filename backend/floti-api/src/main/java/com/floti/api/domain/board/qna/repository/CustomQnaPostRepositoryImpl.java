package com.floti.api.domain.board.qna.repository;

import com.floti.api.domain.board.qna.entity.QQnaPosts;
import com.floti.api.domain.board.qna.entity.QnaPosts;
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
public class CustomQnaPostRepositoryImpl implements CustomQnaPostRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QnaPosts> searchQnaPosts(String search, String sort, Pageable pageable) {
        QQnaPosts qna = QQnaPosts.qnaPost;

        /* 검색 조건 */
        BooleanExpression condition = qna.title.containsIgnoreCase(search)
                .or(qna.content.containsIgnoreCase(search));

        /* 정렬 조건 */
        OrderSpecifier<?>[] sortSpec = switch (sort.toLowerCase()) {
            case "accuracy" -> new OrderSpecifier[]{
                    new CaseBuilder()
                            .when(qna.title.containsIgnoreCase(search))
                            .then(0)
                            .otherwise(1)
                            .asc(),
                    qna.id.desc()
            };
            default -> new OrderSpecifier[] {qna.id.desc()};
        };

        Long totalCount = queryFactory
                .select(qna.count())
                .from(qna)
                .where(condition)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0L;

        List<QnaPosts> content = queryFactory.selectFrom(qna)
                .where(condition)
                .orderBy(sortSpec)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
