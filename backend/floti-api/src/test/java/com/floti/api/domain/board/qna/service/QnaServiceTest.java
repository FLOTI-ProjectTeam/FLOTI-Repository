package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QnaServiceTest {
    @InjectMocks
    private QnaPostService qnaPostService;

    @Mock
    private QnaPostRepository qnaPostRepository;

    @Mock
    private AnswerRepository answerRepository;

    private static final Long VALID_ID = 1L;

    private final Users testUser = Users.builder().id(VALID_ID).nickname("테스터01").build();
    private final QnaPosts testPost = QnaPosts.builder().author(testUser).title("테스트 제목").build();

    @Test
    @DisplayName("getQnaPost: 답변 있음 - 게시글 상세에 답변 포함")
    void getQnaPost_exist() {
        //given
        List<Answers> answers = List.of(
                Answers.builder().author(testUser).content("첫번째 답변").build()
        );

        when(qnaPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.findByPostId(VALID_ID)).thenReturn(answers);

        //when
        QnaPostResponse response = qnaPostService.getQnaPost(VALID_ID);

        //then
        assertEquals("테스트 제목", response.getTitle());
        assertEquals(1, response.getAnswers().size());
        assertEquals("첫번째 답변", response.getAnswers().get(0).getContent());
    }

    @Test
    @DisplayName("getQnaPost: 답변 없음 - 게시글 상세에 빈 리스트 포함")
    void getQnaPost_empty() {
        //given
        when(qnaPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(answerRepository.findByPostId(VALID_ID)).thenReturn(Collections.emptyList());

        //when
        QnaPostResponse response = qnaPostService.getQnaPost(VALID_ID);

        //then
        assertEquals("테스트 제목", response.getTitle());
        assertTrue(response.getAnswers().isEmpty());
    }
}
