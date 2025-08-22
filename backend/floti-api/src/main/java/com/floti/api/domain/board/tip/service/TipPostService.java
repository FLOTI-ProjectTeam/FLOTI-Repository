package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.like.repository.LikeTipPostRepository;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.PostNotFoundException;
import com.floti.api.error.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TipPostService {
    private final TipPostRepository tipPostRepository;
    private final UserRepository userRepository;
    private final LikeTipPostRepository likeTipPostRepository;

    /* 1. 조회 & 검색 */
    public Page<TipPostResponse> getTipPosts(String search, Pageable pageable) {
        Page<TipPosts> tipPostPage;

        if (search != null && !search.isBlank()) {
            tipPostPage = tipPostRepository.findByTitleContainingIgnoreCase(pageable, search);
        } else {
            tipPostPage = tipPostRepository.findAll(pageable);
        }

        return tipPostPage.map(TipPostResponse::new);
    }

    /* 2. 상세 조회 */
    public TipPostResponse getTipPost(Long userId, Long id) {
        TipPosts tipPost = tipPostRepository.findById(id).orElseThrow(PostNotFoundException::new);
        boolean liked = likeTipPostRepository.existsByUserIdAndPostId(userId, id);
        return new TipPostResponse(tipPost, liked);
    }

    /* 3. 등록 */
    @Transactional
    public TipPostResponse createTipPost(Long userId, PostRequest postRequest, MultipartFile file) {
        Users author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        TipPosts tipPost = TipPosts.builder()
                .author(author)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();

        return new TipPostResponse(tipPostRepository.save(tipPost));
    }

    /* 4. 수정 */
    @Transactional
    public TipPostResponse updateTipPost(Long userId, Long id, PostRequest postRequest) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        TipPosts tipPost = tipPostRepository.findById(id).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(tipPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.POST_UPDATE_DENIED);

        tipPost.update(postRequest);
        return new TipPostResponse(tipPost);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteTipPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        TipPosts tipPost = tipPostRepository.findById(id).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(tipPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.POST_DELETE_DENIED);

        tipPostRepository.delete(tipPost);
    }
}
