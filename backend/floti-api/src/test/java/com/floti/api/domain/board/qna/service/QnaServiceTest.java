package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test") //application-test.yml 사용
@Transactional
public class QnaServiceTest {
    @Autowired
    private QnaPostService qnaPostService;

    @Autowired
    private QnaPostRepository qnaPostRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testPostId;
    private Long testAnswerId;

    @BeforeEach
        //테스트용 데이터 생성
    void setUp() {
        Users user = Users.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        userRepository.save(user);
        testUserId = user.getId();

        QnaPosts qnaPost = QnaPosts.builder()
                .author(user)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        qnaPostRepository.save(qnaPost);
        testPostId = qnaPost.getId();

        Answers answer = Answers.builder()
                .postId(testPostId)
                .author(user)
                .content("답변 내용")
                .build();
        answerRepository.save(answer);
        qnaPost.incrementAnswerCount();
        testAnswerId = answer.getId();
    }

    @Test
    @DisplayName("getQnaPost: 답변 있는 게시글 상세 조회")
    void getQnaPost_existAnswer() {
        QnaPostResponse response = qnaPostService.getQnaPost(testPostId);

        assertEquals("원본 제목", response.getTitle());
        assertEquals("원본 내용", response.getContent());
        assertEquals(1, response.getAnswers().size());
        assertEquals("답변 내용", response.getAnswers().get(0).getContent());
    }

    @Test
    @DisplayName("getQnaPost: 답변 없는 게시글 상세 조회")
    void getQnaPost_emptyAnswer() {
        QnaPosts qnaPost = QnaPosts.builder()
                .author(userRepository.findById(testUserId).get())
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        qnaPostRepository.save(qnaPost);

        QnaPostResponse response = qnaPostService.getQnaPost(qnaPost.getId());

        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스트 내용", response.getContent());
        assertTrue(response.getAnswers().isEmpty());
    }
}
