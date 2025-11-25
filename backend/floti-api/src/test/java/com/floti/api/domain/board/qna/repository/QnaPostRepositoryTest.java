package com.floti.api.domain.board.qna.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslConfig.class)
public class QnaPostRepositoryTest {
    @Autowired
    private QnaPostRepository qnaPostRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        em.persist(testUser);

        QnaPosts post1 = QnaPosts.builder()
                .author(testUser)
                .title("채택된 제목")
                .content("테스트 내용")
                .accepted(true)
                .build();
        QnaPosts post2 = QnaPosts.builder()
                .author(testUser)
                .title("채택된 제목")
                .content("테스트 내용")
                .accepted(true)
                .build();
        QnaPosts post3 = QnaPosts.builder()
                .author(testUser)
                .title("미채택된 제목")
                .content("테스트 내용")
                .build();
        QnaPosts post4 = QnaPosts.builder()
                .author(testUser)
                .title("미채택된 제목")
                .content("테스트 내용")
                .build();
        qnaPostRepository.saveAll(List.of(post1, post2, post3, post4));

        for (int i=0; i<10; i++) post1.incrementAnswerCount();
        for (int i=0; i<5; i++) post2.incrementAnswerCount();
        for (int i=0; i<3; i++) post4.incrementAnswerCount();
    }

    @Test
    @DisplayName("getQnaPosts: 미채택 최신순 - 최신 QnaPosts 페이지 먼저 반환")
    void getQnaPosts_latest_notAccepted() {
        // given
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());

        // when
        Page<QnaPosts> result = qnaPostRepository.findAll(true, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(3, result.getContent().get(0).getAnswerCount());
        assertTrue(result.getContent().stream().noneMatch(QnaPosts::isAccepted));
    }

    @Test
    @DisplayName("findAll: 미채택 답변적은순 - 답변 적은 QnaPosts 페이지 먼저 반환")
    void findAll_answersAsc_notAccepted() {
        // given
        Pageable pageable = PageRequest.of(0, 20, Sort.by("answerCount").ascending());

        // when
        Page<QnaPosts> result = qnaPostRepository.findAll(true, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getContent().get(0).getAnswerCount());
        assertTrue(result.getContent().stream().noneMatch(QnaPosts::isAccepted));
    }

    @Test
    @DisplayName("findAll: 미채택 답변많은순 - 답변 많은 QnaPosts 페이지 먼저 반환")
    void findAll_answersDesc_notAccepted() {
        // given
        Pageable pageable = PageRequest.of(0, 20, Sort.by("answerCount").descending());

        // when
        Page<QnaPosts> result = qnaPostRepository.findAll(true, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(3, result.getContent().get(0).getAnswerCount());
        assertTrue(result.getContent().stream().noneMatch(QnaPosts::isAccepted));
    }
}
