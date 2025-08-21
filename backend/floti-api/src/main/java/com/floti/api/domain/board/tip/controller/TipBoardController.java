package com.floti.api.domain.board.tip.controller;

import com.floti.api.domain.board.common.dto.LikeResponse;
import com.floti.api.domain.board.common.dto.PostCreateRequest;
import com.floti.api.domain.board.common.dto.PostUpdateRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.service.TipPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/tip")
public class TipBoardController {
    private final TipPostService tipPostService;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<TipPostResponse> getTipPosts(@RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String search) {
        Pageable pageable;
        if ("registered".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("id").descending());
        }
        return tipPostService.getTipPosts(search, pageable);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<TipPostResponse> getTipPost(@PathVariable Long id) {
        TipPostResponse response = tipPostService.getTipPost(id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<TipPostResponse> createTipPost(@Validated @RequestPart PostCreateRequest post,
                                                         @RequestPart(required = false) MultipartFile file) {
        TipPostResponse response = tipPostService.createTipPost(post.getAuthorId(), post, file);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping
    public ResponseEntity<TipPostResponse> updateTipPost(@Validated PostUpdateRequest post) {
        TipPostResponse response = tipPostService.updateTipPost(post.getAuthorId(), post);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<TipPostResponse> deleteTipPost(@RequestParam Long userId, //임시
                                                         @PathVariable Long id) {
        tipPostService.deleteTipPost(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }

    /* 6. 좋아요 처리 */
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> toggleLike(@RequestParam Long userId, //임시
                                                   @PathVariable Long id) {
        LikeResponse response = tipPostService.toggleLike(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
