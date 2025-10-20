package com.floti.api.domain.board.discussion.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.DiscussionPostDetailResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionPostRequest;
import com.floti.api.domain.board.discussion.dto.DiscussionPostResponse;
import com.floti.api.domain.board.discussion.service.DiscussionPostService;
import com.floti.api.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/discussions")
public class DiscussionPostController {
    private final DiscussionPostService discussionPostService;
    private final AuthUtil authUtil;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<DiscussionPostResponse> getDiscussionPosts(@RequestParam(required = false) String search,
                                                           @RequestParam(defaultValue = "latest") String sort,
                                                           @RequestParam(defaultValue = "0") int page) {
        sort = sort.trim();
        if (search == null || search.isBlank())
            return discussionPostService.getDiscussionPosts(sort, page);
        return discussionPostService.searchDiscussionPosts(search.trim(), sort, page);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionPostDetailResponse> getDiscussionPost(@AuthenticationPrincipal UserDetails userDetails,
                                                                          @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        DiscussionPostDetailResponse response = discussionPostService.getDiscussionPost(userId, id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<DiscussionPostResponse> createDiscussionPost(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @Validated @RequestBody DiscussionPostRequest post) {
        User user = authUtil.resolveUser(userDetails);
        DiscussionPostResponse response = discussionPostService.createDiscussionPost(user, post);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionPostResponse> updateDiscussionPost(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @PathVariable long id,
                                                                       @Validated @RequestBody DiscussionPostRequest post) {
        Long userId = authUtil.resolveUserId(userDetails);
        DiscussionPostResponse response = discussionPostService.updateDiscussionPost(userId, id, post);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussionPost(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        discussionPostService.deleteDiscussionPost(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }

    /* 6. 참여 토글 */
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> toggleJoinDiscussion(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        discussionPostService.toggleJoinDiscussion(userId, id);
        return ResponseEntity.ok().build();
    }
}
