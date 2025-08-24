package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.qna.dto.AnswerRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.error.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {
    @InjectMocks
    private AnswerService answerService;

    @Mock
    private QnaPostRepository qnaPostRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private UserRepository userRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final Users testUser = Users.builder().id(VALID_ID).nickname("테스터01").build();
    private final QnaPosts testPost = QnaPosts.builder().author(testUser).build();
    private final Answers testAnswer = Answers.builder()
            .id(VALID_ID).postId(VALID_ID).author(testUser).content("첫번째 답변").build();

    @BeforeEach
    void setUp() {
        testPost.incrementAnswerCount();
    }

    @Test
    @DisplayName("createAnswer: 답변 등록")
    void createAnswer_success() {
        //given
        AnswerRequest request = new AnswerRequest();
        request.setContent("두번째 답변");

        int previousAnswerCount = testPost.getAnswerCount();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(testUser));
        when(qnaPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.save(any(Answers.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        AnswerResponse response = answerService.createAnswer(VALID_ID, VALID_ID, request);

        //then
        assertEquals(VALID_ID, response.getPostId());
        assertEquals("두번째 답변", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(previousAnswerCount + 1, testPost.getAnswerCount());
    }

    @Test
    @DisplayName("createAnswer: 채택된 게시글에 답변 등록 - PostAlreadyClosedException")
    void createAnswer_fail_acceptedPost() {
        //given
        AnswerRequest request = new AnswerRequest();

        testPost.accept(testAnswer);

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(testUser));
        when(qnaPostRepository.findById(INVALID_ID)).thenReturn(Optional.of(testPost));

        //when
        PostAlreadyClosedException exception = assertThrows(PostAlreadyClosedException.class, () -> {
            answerService.createAnswer(VALID_ID, INVALID_ID, request);
        });

        //then
        assertEquals("채택된 게시글에는 답변할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateAnswer: 답변 수정")
    void updateAnswer_success() {
        //given
        AnswerRequest request = new AnswerRequest();
        request.setContent("수정된 답변");

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(answerRepository.findById(VALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        AnswerResponse response = answerService.updateAnswer(VALID_ID, VALID_ID, request);

        //then
        assertEquals("수정된 답변", response.getContent());
    }

    @Test
    @DisplayName("updateAnswer: 없는 답변 수정 - AnswerNotFoundException")
    void updateAnswer_fail_answerNotFound() {
        //given
        AnswerRequest request = new AnswerRequest();

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(answerRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        //when
        AnswerNotFoundException exception = assertThrows(AnswerNotFoundException.class, () -> {
            answerService.updateAnswer(VALID_ID, INVALID_ID, request);
        });

        //then
        assertEquals("답변을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateAnswer: 채택된 답변 수정 - AcceptedAnswerUpdateException")
    void updateAnswer_fail_acceptedAnswer() {
        //given
        AnswerRequest request = new AnswerRequest();

        testAnswer.accept();

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(answerRepository.findById(INVALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        AcceptedAnswerUpdateException exception = assertThrows(AcceptedAnswerUpdateException.class, () -> {
            answerService.updateAnswer(VALID_ID, INVALID_ID, request);
        });

        //then
        assertEquals("채택된 답변은 수정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteAnswer: 답변 삭제")
    void deleteAnswer_success() {
        // given
        int previousAnswerCount = testPost.getAnswerCount();

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(answerRepository.findById(VALID_ID)).thenReturn(Optional.of(testAnswer));
        when(qnaPostRepository.getReferenceById(VALID_ID)).thenReturn(testPost);

        // when
        answerService.deleteAnswer(VALID_ID, VALID_ID);

        //then
        verify(answerRepository).delete(testAnswer);
        assertEquals(previousAnswerCount - 1, testPost.getAnswerCount());
    }

    @Test
    @DisplayName("deleteAnswer: 채택된 답변 삭제 - AcceptedAnswerDeletionException")
    void deleteAnswer_fail_acceptedAnswer() {
        //given
        testAnswer.accept();

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(answerRepository.findById(INVALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        AcceptedAnswerDeletionException exception = assertThrows(AcceptedAnswerDeletionException.class, () -> {
            answerService.deleteAnswer(VALID_ID, INVALID_ID);
        });

        //then
        assertEquals("채택된 답변은 삭제할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("acceptAnswer: 작성자 맞음 - 답변 채택")
    void acceptAnswer_success() {
        //given
        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(qnaPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.findById(VALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        answerService.acceptAnswer(VALID_ID, VALID_ID, VALID_ID);

        //then
        assertTrue(testPost.isAccepted());
        assertTrue(testAnswer.isAccepted());
    }

    @Test
    @DisplayName("acceptAnswer: 작성자 아님 - AccessDeniedException")
    void acceptAnswer_fail_authorMismatch() {
        //given
        when(userRepository.existsById(INVALID_ID)).thenReturn(true);
        when(qnaPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.findById(VALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            answerService.acceptAnswer(INVALID_ID, VALID_ID, VALID_ID);
        });

        //then
        assertEquals("채택할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("acceptAnswer: 채택된 게시글 답변 채택 - PostAlreadyAcceptedException")
    void acceptAnswer_fail_acceptedPost() {
        //given
        testPost.accept(testAnswer);

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(qnaPostRepository.findById(INVALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.findById(VALID_ID)).thenReturn(Optional.of(testAnswer));

        //when
        PostAlreadyAcceptedException exception = assertThrows(PostAlreadyAcceptedException.class, () -> {
            answerService.acceptAnswer(VALID_ID, INVALID_ID, VALID_ID);
        });

        //then
        assertEquals("이미 채택된 게시글입니다.", exception.getMessage());
    }
}
