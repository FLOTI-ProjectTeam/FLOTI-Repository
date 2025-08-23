package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.qna.dto.AnswerRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.error.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") //application-test.yml 사용
@Transactional
public class AnswerServiceTest {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private QnaPostRepository qnaPostRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testPostId;
    private Long testAnswerId;

    @BeforeEach //테스트용 데이터 생성
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
    @DisplayName("createAnswer: 답변 등록")
    void createAnswer_success() {
        AnswerRequest request = new AnswerRequest();
        request.setContent("등록된 내용");

        AnswerResponse response = answerService.createAnswer(testUserId, testPostId, request);
        QnaPosts qnaPost = qnaPostRepository.findById(response.getPostId()).get();

        assertNotNull(response.getId());
        assertEquals(testPostId, response.getPostId());
        assertEquals("등록된 내용", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(2, qnaPost.getAnswerCount());
    }

    @Test
    @DisplayName("updateAnswer: 답변 수정")
    void updateAnswer_success() {
        AnswerRequest request = new AnswerRequest();
        request.setContent("수정된 내용");

        AnswerResponse response = answerService.updateAnswer(testUserId, testAnswerId, request);

        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("updateAnswer: 답변 수정 [답변 없음]")
    void updateAnswer_fail_answerNotFound() {
        AnswerRequest request = new AnswerRequest();
        request.setContent("수정된 내용");

        AnswerNotFoundException exception = assertThrows(AnswerNotFoundException.class, () -> {
            answerService.updateAnswer(testUserId, 9999L, request);
        });

        assertEquals("답변을 찾을 수 없습니다.", exception.getMessage());
    }

    @Autowired
    private QnaPostService qnaPostService;

    @Test
    @DisplayName("deleteAnswer: 답변 삭제")
    void deleteAnswer_success() {
        answerService.deleteAnswer(testUserId, testAnswerId);

        QnaPostResponse response = qnaPostService.getQnaPost(testPostId);

        assertEquals(0, response.getAnswerCount());
        assertTrue(response.getAnswers().isEmpty());
    }

    @Test
    @DisplayName("acceptAnswer: 답변 채택")
    void acceptAnswer_success() {
        answerService.acceptAnswer(testUserId, testPostId, testAnswerId);

        QnaPostResponse response = qnaPostService.getQnaPost(testPostId);

        assertTrue(response.isAccepted());
        assertTrue(response.getAnswers().get(0).isAccepted());
    }

    @Test
    @DisplayName("acceptAnswer: 답변 채택 [작성자 불일치]")
    void acceptAnswer_fail_authorMismatch() {
        Users user = Users.builder()
                .email("test02@gmail.com")
                .username("test02")
                .password("password123")
                .nickname("테스터02")
                .build();
        userRepository.save(user);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            answerService.acceptAnswer(user.getId(), testPostId, testAnswerId);
        });

        assertEquals("채택할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("acceptAnswer: 답변 채택 [채택된 게시글]")
    void acceptAnswer_fail_postAccepted() {
        answerService.acceptAnswer(testUserId, testPostId, testAnswerId);

        PostAlreadyAcceptedException exception = assertThrows(PostAlreadyAcceptedException.class, () -> {
            answerService.acceptAnswer(testUserId, testPostId, testAnswerId);
        });

        assertEquals("이미 채택된 게시글입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createAnswer: 답변 등록 [채택된 게시글]")
    void createAnswer_fail_postAccepted() {
        answerService.acceptAnswer(testUserId, testPostId, testAnswerId);

        AnswerRequest request = new AnswerRequest();
        request.setContent("등록된 내용");

        PostAlreadyClosedException exception = assertThrows(PostAlreadyClosedException.class, () -> {
            answerService.createAnswer(testUserId, testPostId, request);
        });

        assertEquals("채택된 게시글에는 답변할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateAnswer: 답변 수정 [채택된 답변]")
    void updateAnswer_fail() {
        answerService.acceptAnswer(testUserId, testPostId, testAnswerId);

        AnswerRequest request = new AnswerRequest();
        request.setContent("수정된 내용");

        AcceptedAnswerUpdateException exception = assertThrows(AcceptedAnswerUpdateException.class, () -> {
            answerService.updateAnswer(testUserId, testAnswerId, request);
        });

        assertEquals("채택된 답변은 수정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteAnswer: 답변 삭제 [채택된 답변]")
    void deleteAnswer_fail() {
        answerService.acceptAnswer(testUserId, testPostId, testAnswerId);

        AcceptedAnswerDeletionException exception = assertThrows(AcceptedAnswerDeletionException.class, () -> {
            answerService.deleteAnswer(testUserId, testAnswerId);
        });

        assertEquals("채택된 답변은 삭제할 수 없습니다.", exception.getMessage());
    }
}
