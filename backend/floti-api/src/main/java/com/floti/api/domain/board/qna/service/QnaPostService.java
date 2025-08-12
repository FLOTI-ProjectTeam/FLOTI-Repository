package com.floti.api.domain.board.qna.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostCreateRequest;
import com.floti.api.domain.board.common.dto.PostUpdateRequest;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.entity.QnaPosts;
import com.floti.api.domain.board.qna.repository.QnaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QnaPostService {
    private final QnaPostRepository qnaPostRepository;
    private final UserRepository userRepository;

    public Page<QnaPostResponse> getQnaPosts(String search, Pageable pageable) {
        Page<QnaPosts> qnaPostPage;

        if (search != null && !search.isBlank()) {
            qnaPostPage = qnaPostRepository.findByTitleContainingIgnoreCase(pageable, search);
        } else {
            qnaPostPage = qnaPostRepository.findAll(pageable);
        }

        return qnaPostPage.map(QnaPostResponse::new);
    }

    public QnaPostResponse getQnaPost(Long id) {
        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));
        return new QnaPostResponse(qnaPost);
    }

    public QnaPostResponse createQnaPost(Long userId, PostCreateRequest post) {
        Users author = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 정보를 찾을 수 없습니다."));

        QnaPosts qnaPost = QnaPosts.builder()
                .author(author)
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return new QnaPostResponse(qnaPostRepository.save(qnaPost));
    }

    public QnaPostResponse updateQnaPost(Long userId, PostUpdateRequest post) {
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        QnaPosts qnaPost = qnaPostRepository.findById(post.getId())
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (userId.equals(qnaPost.getAuthor().getId())) {
            qnaPost.update(post);
            return new QnaPostResponse(qnaPostRepository.save(qnaPost));
        } else {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }
    }

    public void deleteQnaPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        QnaPosts qnaPost = qnaPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (userId.equals(qnaPost.getAuthor().getId())) {
            qnaPostRepository.delete(qnaPost);
        } else {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
    }
}
