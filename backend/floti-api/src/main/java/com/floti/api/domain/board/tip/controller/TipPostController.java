package com.floti.api.domain.board.tip.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.service.TipPostService;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/tips")
public class TipPostController {
    private final TipPostService tipPostService;
    private final LikeService likeService;

    private final AuthUtil authUtil;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<TipPostResponse> getTipPosts(@RequestParam(required = false) String search,
                                             @RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "0") int page) {
        sort = sort.trim();
        if (search == null || search.isBlank())
            return tipPostService.getTipPosts(sort, page);
        return tipPostService.searchTipPosts(search.trim(), sort, page);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<TipPostResponse> getTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        TipPostResponse response = tipPostService.getTipPost(userId, id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<TipPostResponse> createTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                                         @Validated @RequestPart PostRequest post,
                                                         @RequestPart(required = false) MultipartFile file) {
        User user = authUtil.resolveUser(userDetails);
        TipPostResponse response = tipPostService.createTipPost(user, post, file);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<TipPostResponse> updateTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long id,
                                                         @Validated @RequestPart PostRequest post,
                                                         @RequestPart(required = false) MultipartFile file,
                                                         @RequestPart boolean deleted) {
        User user = authUtil.resolveUser(userDetails);
        TipPostResponse response = tipPostService.updateTipPost(user, id, post, file, deleted);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        tipPostService.deleteTipPost(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }

    /* 6. 추천 토글 */
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> toggleLikeTipPost(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        LikeResponse response = likeService.toggleLikeTipPost(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }
}
