package com.floti.api.domain.board.tip.controller;

import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.service.TipPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TipPostResponse> getTipPost(@RequestParam long userId, //임시,
                                                      @PathVariable long id) {
        TipPostResponse response = tipPostService.getTipPost(userId, id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<TipPostResponse> createTipPost(@Validated @RequestPart PostRequest post,
                                                         @RequestPart(required = false) MultipartFile file) {
        TipPostResponse response = tipPostService.createTipPost(post.getAuthorId(), post, file);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<TipPostResponse> updateTipPost(@Validated @RequestPart PostRequest post,
                                                         @RequestPart(required = false) MultipartFile file,
                                                         @RequestPart boolean deleted,
                                                         @PathVariable long id) {
        TipPostResponse response = tipPostService.updateTipPost(post.getAuthorId(), id, post, file, deleted);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipPost(@RequestParam long userId, //임시
                                              @PathVariable long id) {
        tipPostService.deleteTipPost(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }
}
