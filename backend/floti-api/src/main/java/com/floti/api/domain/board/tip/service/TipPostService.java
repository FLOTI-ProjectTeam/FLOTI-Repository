package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.image.service.ImageService;
import com.floti.api.domain.like.repository.LikeTipPostRepository;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TipPostService {
    @Value("${file.base-url}")
    private String baseUrl;

    private final TipPostRepository tipPostRepository;
    private final LikeTipPostRepository likeTipPostRepository;
    private final ImageService imageService;

    private static final int PAGE_SIZE = 20;
    private static final String THUMBNAIL_DIR = "tips/thumbnail";

    /* 1-1. 조회 */
    public Page<TipPostResponse> getTipPosts(String sort, int page) {
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "likes" -> Sort.by("likeCount").descending();
            case "registered" -> Sort.by("id").ascending();
            default -> Sort.by("id").descending();
        };

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sortOrder);
        Page<TipPosts> tipPostPage = tipPostRepository.findAll(pageable);
        return tipPostPage.map(tipPost -> new TipPostResponse(tipPost, baseUrl));
    }

    /* 1-2. 검색 */
    public Page<TipPostResponse> searchTipPosts(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<TipPosts> tipPostPage = tipPostRepository.searchTipPosts(search, sort, pageable);
        return tipPostPage.map(tipPost -> new TipPostResponse(tipPost, baseUrl));
    }

    /* 2. 상세 조회 */
    public TipPostResponse getTipPost(Long userId, Long id) {
        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        boolean liked = likeTipPostRepository.existsByUserIdAndPostId(userId, id);
        return new TipPostResponse(tipPost, baseUrl, liked);
    }

    /* 3. 등록 */
    @Transactional
    public TipPostResponse createTipPost(User user, PostRequest request, MultipartFile file) {
        TipPosts tipPost = TipPosts.builder()
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        if (file != null && !file.isEmpty()) {
            String thumbnail = imageService.saveImage(file, THUMBNAIL_DIR);
            tipPost.updateThumbnail(thumbnail);
        }

        return new TipPostResponse(tipPostRepository.save(tipPost), baseUrl);
    }

    /* 4. 수정 */
    @Transactional
    public TipPostResponse updateTipPost(User user, Long id, PostRequest request, MultipartFile file, boolean deleted) {
        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!user.getId().equals(tipPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        tipPost.update(request);

        if (file != null && !file.isEmpty()) {
            String newPath = imageService.saveImage(file, THUMBNAIL_DIR);
            imageService.deleteImage(tipPost.getThumbnail());
            tipPost.updateThumbnail(newPath);
        } else if (deleted) {
            imageService.deleteImage(tipPost.getThumbnail());
            tipPost.updateThumbnail(null);
        }

        return new TipPostResponse(tipPost, baseUrl);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteTipPost(Long userId, Long id) {
        TipPosts tipPost = tipPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(tipPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        imageService.deleteImage(tipPost.getThumbnail());
        tipPostRepository.delete(tipPost);
    }
}
