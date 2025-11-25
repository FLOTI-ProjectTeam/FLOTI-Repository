package com.floti.api.domain.board.qna.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.qna.dto.QnaPostDetailResponse;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.qna.service.QnaPostService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/qnas")
public class QnaPostController {
    private final QnaPostService qnaPostService;
    private final AuthUtil authUtil;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<QnaPostResponse> getQnaPosts(@RequestParam(required = false) String search,
                                             @RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "false") boolean unaccepted) {
        sort = sort.trim();
        if (search == null || search.isBlank())
            return qnaPostService.getQnaPosts(sort, page, unaccepted);
        return qnaPostService.searchQnaPosts(search.trim(), sort, page);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<QnaPostDetailResponse> getQnaPost(@AuthenticationPrincipal UserDetails userDetails,
                                                            @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        QnaPostDetailResponse response = qnaPostService.getQnaPost(userId, id);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<QnaPostResponse> createQnaPost(@AuthenticationPrincipal UserDetails userDetails,
                                                         @Validated @RequestBody PostRequest post) {
        User user = authUtil.resolveUser(userDetails);
        QnaPostResponse response = qnaPostService.createQnaPost(user, post);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<QnaPostResponse> updateQnaPost(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable long id,
                                                         @Validated @RequestBody PostRequest post) {
        Long userId = authUtil.resolveUserId(userDetails);
        QnaPostResponse response = qnaPostService.updateQnaPost(userId, id, post);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQnaPost(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        qnaPostService.deleteQnaPost(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
