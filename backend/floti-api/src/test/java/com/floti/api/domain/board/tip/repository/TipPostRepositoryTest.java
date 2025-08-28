package com.floti.api.domain.board.tip.repository;

import com.floti.api.config.QuerydslTestConfig;
import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import com.floti.api.domain.board.tip.entity.QTipPosts;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslTestConfig.class)
public class TipPostRepositoryTest {
    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory queryFactory;

    private Users testUser;
    private TipPosts testPost;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        em.persist(testUser);

        testPost = TipPosts.builder()
                .author(testUser)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        tipPostRepository.save(testPost);
    }

    private Page<TipPosts> searchTipPosts(String search, String sort) {
        QTipPosts tip = QTipPosts.tipPost;

        BooleanExpression condition = tip.title.containsIgnoreCase(search)
                .or(tip.content.containsIgnoreCase(search));

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

        System.out.println(sort);

        Long totalCount = queryFactory
                .select(tip.count())
                .from(tip)
                .where(condition)
                .fetchOne();

        long total = (totalCount != null) ? totalCount : 0L;

        List<TipPosts> content = queryFactory.selectFrom(tip)
                .where(condition)
                .orderBy(sortSpec)
                .fetch();

        return new PageImpl<>(content, PageRequest.of(0, 20), total);
    }

    @Test
    @DisplayName("searchTipPosts: 정확도순 - 제목 포함 TipPosts 페이지 먼저 반환")
    void searchTipPosts_accuracy() {
        //given
        TipPosts post1 = TipPosts.builder()
                .author(testUser)
                .title("검색할 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(post1);

        TipPosts post2 = TipPosts.builder()
                .author(testUser)
                .title("테스트 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(post2);

        Page<TipPosts> result = searchTipPosts("검색", "accuracy");

        //then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTitle().contains("검색"));
        assertFalse(result.getContent().get(1).getTitle().contains("검색"));
        assertTrue(result.getContent().get(1).getContent().contains("검색"));
    }

    @Test
    @DisplayName("searchTipPosts: 추천순 - 추천수 많은 TipPosts 페이지 먼저 반환")
    void searchTipPosts_likes() {
        //given
        TipPosts post1 = TipPosts.builder()
                .author(testUser)
                .title("검색할 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(post1);

        TipPosts post2 = TipPosts.builder()
                .author(testUser)
                .title("검색할 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(post2);

        post1.incrementLikeCount();

        Page<TipPosts> result = searchTipPosts("검색", "likes");

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals(post1.getId(), result.getContent().get(0).getId());
        assertEquals(post2.getId(), result.getContent().get(1).getId());
    }

    @Test
    @DisplayName("searchTipPosts: 영어 검색어 - 대소문자 무시하고 TipPosts 페이지 반환")
    void searchTipPosts_ignoreCase() {
        //given
        TipPosts searchPost = TipPosts.builder()
                .author(testUser)
                .title("Search Title")
                .content("Search Content")
                .build();
        tipPostRepository.save(searchPost);

        //when
        Page<TipPosts> result = searchTipPosts("SEARCH", "latest");

        //then
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("searchTipPosts: 검색어 없음 - 빈 페이지 반환")
    void searchTipPosts_empty() {
        //when
        Page<TipPosts> result = searchTipPosts("검색", "latest");

        //then
        assertTrue(result.isEmpty());
    }
}
