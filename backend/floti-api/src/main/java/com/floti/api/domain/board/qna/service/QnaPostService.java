package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.like.entity.LikeAnswers;
import com.floti.api.domain.board.like.repository.LikeAnswerRepository;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaPostService {
    private final QnaPostRepository qnaPostRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final LikeAnswerRepository likeAnswerRepository;

    private static final int PAGE_SIZE = 20;

    /* 1-1. 조회 */
    public Page<QnaPostResponse> getQnaPosts(String sort, int page) {
        Pageable pageable;
        if (sort.equalsIgnoreCase("registered")) {
            pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        } else {
            pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        }
        ;
        Page<QnaPosts> qnaPostPage = qnaPostRepository.findAll(pageable);
        return qnaPostPage.map(QnaPostResponse::new);
    }

    /* 1-2. 검색 */
    public Page<QnaPostResponse> searchQnaPosts(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<QnaPosts> qnaPostPage = qnaPostRepository.searchQnaPosts(search, sort, pageable);
        return qnaPostPage.map(QnaPostResponse::new);
    }

    /* 2. 상세 조회 */
    public QnaPostResponse getQnaPost(Long userId, Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        List<Answers> answers = answerRepository.findByPostId(id);

        /* 답변별 추천 여부 */
        // 1. 모든 답변 ID
        List<Long> answerIds = answers.stream().map(Answers::getId).toList();

        // 2. 사용자가 추천한 답변 ID
        Set<Long> likedAnswerIds = likeAnswerRepository.findByUserIdAndAnswerIdIn(userId, answerIds)
                .stream()
                .map(LikeAnswers::getAnswerId)
                .collect(Collectors.toSet());

        List<AnswerResponse> answerResponses = answers.stream()
                .map(answer -> new AnswerResponse(answer, likedAnswerIds.contains(answer.getId())))
                .toList();

        return new QnaPostResponse(qnaPost, answerResponses);
    }

    /* 3. 등록 */
    @Transactional
    public QnaPostResponse createQnaPost(Long userId, PostRequest postRequest) {
        Users author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND));

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
            throw new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND);

        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        qnaPost.update(postRequest);
        return new QnaPostResponse(qnaPost);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteQnaPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND);

        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        qnaPostRepository.delete(qnaPost);
    }
}
