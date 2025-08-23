package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.PostNotFoundException;
import com.floti.api.error.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaPostService {
    private final QnaPostRepository qnaPostRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    /* 1. 조회 & 검색 */
    public Page<QnaPostResponse> getQnaPosts(String search, Pageable pageable) {
        Page<QnaPosts> qnaPostPage;

        if (search != null && !search.isBlank()) {
            qnaPostPage = qnaPostRepository.findByTitleContainingIgnoreCase(pageable, search);
        } else {
            qnaPostPage = qnaPostRepository.findAll(pageable);
        }

        return qnaPostPage.map(QnaPostResponse::new);
    }

    /* 2. 상세 조회 */
    public QnaPostResponse getQnaPost(Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(id).orElseThrow(PostNotFoundException::new);
        List<Answers> answers = answerRepository.findByPostId(id);
        return new QnaPostResponse(qnaPost, answers);
    }

    /* 3. 등록 */
    @Transactional
    public QnaPostResponse createQnaPost(Long userId, PostRequest postRequest) {
        Users author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        QnaPosts qnaPost = QnaPosts.builder()
                .author(author)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();

        return new QnaPostResponse(qnaPostRepository.save(qnaPost));
    }

    /* 4. 수정 */
    @Transactional
    public QnaPostResponse updateQnaPost(Long userId, Long id, PostRequest postRequest) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        QnaPosts qnaPost = qnaPostRepository.findById(id).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        qnaPost.update(postRequest);
        return new QnaPostResponse(qnaPost);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteQnaPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        QnaPosts qnaPost = qnaPostRepository.findById(id).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        qnaPostRepository.delete(qnaPost);
    }
}
