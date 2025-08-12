package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.board.common.dto.PostCreateRequest;
import com.floti.api.domain.board.common.dto.PostUpdateRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TipPostService {
    private final TipPostRepository tipPostRepository;
    private final UserRepository userRepository;

    public Page<TipPostResponse> getTipPosts(String search, Pageable pageable) {
        Page<TipPosts> tipPostPage;

        if (search != null && !search.isBlank()) {
            tipPostPage = tipPostRepository.findByTitleContainingIgnoreCase(pageable, search);
        } else {
            tipPostPage = tipPostRepository.findAll(pageable);
        }

        return tipPostPage.map(TipPostResponse::new);
    }

    public TipPostResponse getTipPost(Long id) {
        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));
        return new TipPostResponse(tipPost);
    }

    public TipPostResponse createTipPost(Long userId, PostCreateRequest post, MultipartFile file) {
        Users author = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 정보를 찾을 수 없습니다."));

        TipPosts tipPost = TipPosts.builder()
                .author(author)
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return new TipPostResponse(tipPostRepository.save(tipPost));
    }

    public TipPostResponse updateTipPost(Long userId, PostUpdateRequest post) {
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        TipPosts tipPost = tipPostRepository.findById(post.getId())
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (userId.equals(tipPost.getAuthor().getId())) {
            tipPost.update(post);
            return new TipPostResponse(tipPostRepository.save(tipPost));
        } else {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }
    }

    public void deleteTipPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (userId.equals(tipPost.getAuthor().getId())) {
            tipPostRepository.delete(tipPost);
        } else {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
    }
}
