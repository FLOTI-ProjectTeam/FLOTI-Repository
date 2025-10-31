package com.floti.api.domain.board.discussion.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomDetailResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomRequest;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomResponse;
import com.floti.api.domain.board.discussion.service.DiscussionRoomService;
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
public class DiscussionRoomController {
    private final DiscussionRoomService discussionRoomService;
    private final AuthUtil authUtil;

    /* 1. 조회 & 검색 */
    @GetMapping
    public Page<DiscussionRoomResponse> getDiscussionRooms(@RequestParam(required = false) String search,
                                                           @RequestParam(defaultValue = "recentActivity") String sort,
                                                           @RequestParam(defaultValue = "0") int page) {
        sort = sort.trim();
        if (search == null || search.isBlank())
            return discussionRoomService.getDiscussionRooms(sort, page);
        return discussionRoomService.searchDiscussionRooms(search.trim(), sort, page);
    }

    /* 2. 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionRoomDetailResponse> getDiscussionRoom(@AuthenticationPrincipal UserDetails userDetails,
                                                                          @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        DiscussionRoomDetailResponse response = discussionRoomService.getDiscussionRoom(userId, id);
        return ResponseEntity.ok(response);
    }

    /* 3. 등록 */
    @PostMapping
    public ResponseEntity<DiscussionRoomResponse> createDiscussionRoom(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @Validated @RequestBody DiscussionRoomRequest room) {
        User user = authUtil.resolveUser(userDetails);
        DiscussionRoomResponse response = discussionRoomService.createDiscussionRoom(user, room);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 4. 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionRoomResponse> updateDiscussionRoom(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @PathVariable long id,
                                                                       @Validated @RequestBody DiscussionRoomRequest room) {
        Long userId = authUtil.resolveUserId(userDetails);
        DiscussionRoomResponse response = discussionRoomService.updateDiscussionRoom(userId, id, room);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 5. 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussionRoom(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        discussionRoomService.deleteDiscussionRoom(userId, id);
        return ResponseEntity.status(NO_CONTENT).build(); // 204 No Content
    }

    /* 6. 참여 토글 */
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> toggleJoinDiscussion(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long id) {
        User user = authUtil.resolveUser(userDetails);
        discussionRoomService.toggleJoinDiscussion(user, id);
        return ResponseEntity.ok().build();
    }
}
