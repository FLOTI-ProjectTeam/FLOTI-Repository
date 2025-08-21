package com.floti.api.domain.board.qna.controller;

import com.floti.api.domain.board.common.dto.PostCreateRequest;
import com.floti.api.domain.board.common.dto.PostUpdateRequest;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.service.QnaPostService;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/qna")
public class QnaBoardController {
    private final QnaPostService qnaPostService;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<QnaPostResponse> getQnaPosts(@RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String search) {
        Pageable pageable;
        if ("registered".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("id").descending());
        }
        return qnaPostService.getQnaPosts(search, pageable);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<QnaPostResponse> getTipPost(@PathVariable Long id) {
        QnaPostResponse response = qnaPostService.getQnaPost(id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<QnaPostResponse> createTipPost(@Validated PostCreateRequest post) {
        QnaPostResponse response = qnaPostService.createQnaPost(post.getAuthorId(), post);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping
    public ResponseEntity<QnaPostResponse> updateTipPost(@Validated PostUpdateRequest post) {
        QnaPostResponse response = qnaPostService.updateQnaPost(post.getAuthorId(), post);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<TipPostResponse> deleteTipPost(@RequestParam Long userId, //임시
                                                         @PathVariable Long id) {
        qnaPostService.deleteQnaPost(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }
}
