package com.floti.api.domain.board.service;

import com.floti.api.domain.board.dto.TipPostCreateRequest;
import com.floti.api.domain.board.dto.TipPostResponse;
import com.floti.api.domain.board.dto.TipPostUpdateRequest;
import com.floti.api.domain.board.entity.TipPosts;
import com.floti.api.domain.board.repository.TipPostRepository;
import com.floti.api.temp.entity.Users;
import com.floti.api.temp.repository.UserRepository;
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
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));
        return new TipPostResponse(tipPost);
    }

    public TipPostResponse createTipPost(Long userId, TipPostCreateRequest post, MultipartFile thumbnail) {
        if (userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        TipPosts tipPost = TipPosts.builder()
                .author(Users.builder().id(userId).build())
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return new TipPostResponse(tipPostRepository.save(tipPost));
    }

    public TipPostResponse updateTipPost(Long userId, TipPostUpdateRequest post) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");
        }

        TipPosts tipPost = tipPostRepository.findById(post.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        tipPost.setTitle(post.getTitle());
        tipPost.setContent(post.getContent());

        return new TipPostResponse(tipPostRepository.save(tipPost));
    }

    public void deleteTipPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("사용자 정보를 찾을 수 없습니다.");

        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        if (userId.equals(tipPost.getAuthor().getId())) {
            tipPostRepository.delete(tipPost);
        } else {
            throw new AccessDeniedException("해당 게시글을 삭제할 권한이 없습니다.");
        }
    }
}
