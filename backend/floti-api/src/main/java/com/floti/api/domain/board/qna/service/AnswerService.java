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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QnaPostRepository qnaPostRepository;
    private final UserRepository userRepository;

    /* 1. 등록 */
    @Transactional
    public AnswerResponse createAnswer(Long userId, Long postId, AnswerRequest answerRequest) {
        Users author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        QnaPosts qnaPost = qnaPostRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (qnaPost.isAccepted())
            throw new PostAlreadyClosedException();

        Answers answer = Answers.builder()
                .postId(postId)
                .author(author)
                .content(answerRequest.getContent())
                .build();

        answerRepository.save(answer);
        qnaPost.incrementAnswerCount();

        return new AnswerResponse(answer);
    }

    /* 2. 수정 */
    @Transactional
    public AnswerResponse updateAnswer(Long userId, Long id, AnswerRequest answerRequest) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Answers answer = answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);

        if (!userId.equals(answer.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        if (answer.isAccepted())
            throw new AcceptedAnswerUpdateException();

        answer.update(answerRequest);
        return new AnswerResponse(answer);
    }

    /* 3. 삭제 */
    @Transactional
    public void deleteAnswer(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Answers answer = answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);

        if (!userId.equals(answer.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        if (answer.isAccepted()) {
            throw new AcceptedAnswerDeletionException();
        } else {
            QnaPosts qnaPost = qnaPostRepository.getReferenceById(answer.getPostId());
            answerRepository.delete(answer);
            qnaPost.decrementAnswerCount();
        }
    }

    /* 4. 채택 */
    @Transactional
    public void acceptAnswer(Long userId, Long postId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        QnaPosts qnaPost = qnaPostRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Answers answer = answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new);

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException("채택할 권한이 없습니다.");

        qnaPost.accept(answer);
    }
}
