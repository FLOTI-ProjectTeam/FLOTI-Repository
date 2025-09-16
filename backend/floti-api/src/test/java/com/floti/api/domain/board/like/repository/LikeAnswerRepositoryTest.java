package com.floti.api.domain.board.like.repository;

import com.floti.api.config.QuerydslTestConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.like.entity.LikeAnswers;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.like.repository.LikeAnswerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslTestConfig.class)
public class LikeAnswerRepositoryTest {
    @Autowired
    private LikeAnswerRepository likeAnswerRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private QnaPosts testPost;
    private Answers testAnswer;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        em.persist(testUser);

        testPost = QnaPosts.builder()
                .author(testUser)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        em.persist(testPost);

        testAnswer = Answers.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 답변")
                .build();
        em.persist(testAnswer);
    }

    @Test
    @DisplayName("findByUserIdAndAnswerIdIn: 추천 있음 - LikeAnswers 리스트 반환")
    void findByUserIdAndAnswerIdIn_exist() {
        //given
        LikeAnswers likeAnswer = new LikeAnswers(testUser.getId(), testAnswer.getId());
        likeAnswerRepository.save(likeAnswer);

        List<Long> answerIds = List.of(testAnswer.getId());

        //when
        List<LikeAnswers> result = likeAnswerRepository.findByUserIdAndAnswerIdIn(testUser.getId(), answerIds);

        //then
        assertEquals(1, result.size());
        assertEquals(testAnswer.getId(), result.get(0).getAnswerId());
    }

    @Test
    @DisplayName("findByUserIdAndAnswerIdIn: 추천 없음 - 빈 리스트 반환")
    void findByUserIdAndAnswerIdIn_empty() {
        //given
        LikeAnswers likeAnswer = new LikeAnswers(testUser.getId(), testAnswer.getId());
        likeAnswerRepository.save(likeAnswer);

        List<Long> answerIds = List.of(9999L);

        //when
        List<LikeAnswers> result = likeAnswerRepository.findByUserIdAndAnswerIdIn(testUser.getId(), answerIds);

        //then
        assertTrue(result.isEmpty());
    }
}
