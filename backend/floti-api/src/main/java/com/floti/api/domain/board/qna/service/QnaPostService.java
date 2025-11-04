package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.dto.QnaPostDetailResponse;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.Answers;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import com.floti.api.domain.like.entity.LikeAnswers;
import com.floti.api.domain.like.repository.LikeAnswerRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaPostService {
    private final QnaPostRepository qnaPostRepository;
    private final AnswerRepository answerRepository;
    private final LikeAnswerRepository likeAnswerRepository;

    private static final int PAGE_SIZE = 20;

    /* 1-1. 조회 */
    public Page<QnaPostResponse> getQnaPosts(String sort, int page, boolean accepted) {
        Sort.Order baseOrder = Sort.Order.desc("id");
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "answers" -> accepted
                    ? Sort.by(Sort.Order.desc("answerCount"), baseOrder)
                    : Sort.by(Sort.Order.asc("answerCount"), baseOrder);
            default -> Sort.by(baseOrder);
        };

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sortOrder);
        Page<QnaPosts> qnaPostPage = qnaPostRepository.findByAccepted(accepted, pageable);
        return qnaPostPage.map(QnaPostResponse::new);
    }

    /* 1-2. 검색 */
    public Page<QnaPostResponse> searchQnaPosts(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<QnaPosts> qnaPostPage = qnaPostRepository.searchQnaPosts(search, sort, pageable);
        return qnaPostPage.map(QnaPostResponse::new);
    }

    /* 2. 상세 조회 */
    public QnaPostDetailResponse getQnaPost(Long userId, Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        List<Answers> answers = answerRepository.findByPostId(id);

        /* 답변별 좋아요 여부 */
        // 1. 모든 답변 ID
        List<Long> answerIds = answers.stream().map(Answers::getId).toList();

        // 2. 사용자가 좋아요한 답변 ID
        Set<Long> likedAnswerIds = likeAnswerRepository.findByUserIdAndAnswerIdIn(userId, answerIds)
                .stream()
                .map(LikeAnswers::getAnswerId)
                .collect(Collectors.toSet());

        List<AnswerResponse> answerResponses = answers.stream()
                .map(a -> new AnswerResponse(a, likedAnswerIds.contains(a.getId())))
                .toList();

        return new QnaPostDetailResponse(qnaPost, answerResponses);
    }

    /* 3. 등록 */
    @Transactional
    public QnaPostDetailResponse createQnaPost(User user, PostRequest request) {
        QnaPosts qnaPost = QnaPosts.builder()
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return new QnaPostDetailResponse(qnaPostRepository.save(qnaPost), Collections.emptyList());
    }

    /* 4. 수정 */
    @Transactional
    public QnaPostResponse updateQnaPost(Long userId, Long id, PostRequest request) {
        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        qnaPost.update(request.getTitle(), request.getContent());
        return new QnaPostResponse(qnaPost);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteQnaPost(Long userId, Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(qnaPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        qnaPostRepository.delete(qnaPost);
    }
}
