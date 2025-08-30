package com.floti.api.domain.board.qna.controller;

import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.service.QnaPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/qna/posts")
public class QnaPostController {
    private final QnaPostService qnaPostService;

    /* 1. 조회 & 검색 - 추후 변경 예정 */
    @GetMapping
    public Page<QnaPostResponse> getQnaPosts(@RequestParam(required = false) String search,
                                             @RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "0") int page) {
        sort = sort.trim();
        if (search == null || search.isBlank())
            return qnaPostService.getQnaPosts(sort, page);
        return qnaPostService.searchQnaPosts(search.trim(), sort, page);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<QnaPostResponse> getQnaPost(@RequestParam Long userId, //임시
                                                      @PathVariable Long id) {
        QnaPostResponse response = qnaPostService.getQnaPost(userId, id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<QnaPostResponse> createQnaPost(@Validated @RequestBody PostRequest post) {
        QnaPostResponse response = qnaPostService.createQnaPost(post.getAuthorId(), post);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<QnaPostResponse> updateQnaPost(@Validated @RequestBody PostRequest post,
                                                         @PathVariable Long id) {
        QnaPostResponse response = qnaPostService.updateQnaPost(post.getAuthorId(), id, post);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQnaPost(@RequestParam Long userId, //임시
                                              @PathVariable Long id) {
        qnaPostService.deleteQnaPost(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
