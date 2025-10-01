package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.qna.dto.AnswerRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.exception.AcceptedAnswerDeletionException;
import com.floti.api.error.exception.AcceptedAnswerUpdateException;
import com.floti.api.error.exception.PostAlreadyClosedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QnaPostRepository qnaPostRepository;

    /* 1. 등록 */
    @Transactional
    public AnswerResponse createAnswer(User user, Long postId, AnswerRequest request) {
        QnaPosts qnaPost = qnaPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (qnaPost.isAccepted())
            throw new PostAlreadyClosedException();

        Answers answer = Answers.builder()
                .postId(postId)
                .author(user)
                .content(request.getContent())
                .build();

        answerRepository.save(answer);
        qnaPost.incrementAnswerCount();

        return new AnswerResponse(answer);
    }

    /* 2. 수정 */
    @Transactional
    public AnswerResponse updateAnswer(Long userId, Long id, AnswerRequest request) {
        Answers answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ANSWER_NOT_FOUND));

        if (!userId.equals(answer.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        if (answer.isAccepted())
            throw new AcceptedAnswerUpdateException();

        answer.update(request);
        return new AnswerResponse(answer);
    }

    /* 3. 삭제 */
    @Transactional
    public void deleteAnswer(Long userId, Long id) {
        Answers answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ANSWER_NOT_FOUND));

        if (!userId.equals(answer.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        if (answer.isAccepted())
            throw new AcceptedAnswerDeletionException();

        QnaPosts qnaPost = qnaPostRepository.getReferenceById(answer.getPostId());
        answerRepository.delete(answer);
        qnaPost.decrementAnswerCount();
    }

    /* 4. 채택 */
    @Transactional
    public void acceptAnswer(Long userId, Long postId, Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        Answers answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ANSWER_NOT_FOUND));

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException("채택할 권한이 없습니다.");

        qnaPost.accept(answer);
    }
}
