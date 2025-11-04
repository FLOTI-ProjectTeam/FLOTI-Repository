package com.floti.api.domain.board.discussion.controller;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.ErrorMessage;
import com.floti.api.domain.board.discussion.dto.MessageDeleteResponse;
import com.floti.api.domain.board.discussion.dto.MessageRequest;
import com.floti.api.domain.board.discussion.dto.MessageResponse;
import com.floti.api.domain.board.discussion.service.MessageService;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.error.exception.MessageDeleteDeniedException;
import com.floti.api.error.exception.UserNotFoundException;
import com.floti.api.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final LikeService likeService;
    private final AuthUtil authUtil;

    /* 1. 조회 */
    @GetMapping("/community/discussions/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@AuthenticationPrincipal UserDetails userDetails,
                                                             @PathVariable Long roomId,
                                                             @RequestParam Long last) {
        Long userId = authUtil.resolveUserId(userDetails);
        List<MessageResponse> messages = messageService.getMessages(userId, roomId, last);
        return ResponseEntity.ok(messages);
    }

    /* 2. 등록 */
    @MessageMapping("/community/discussions/{roomId}/messages")
    @SendTo("/topic/community/discussions/{roomId}/messages")
    public MessageResponse createMessage(@AuthenticationPrincipal UserDetails userDetails,
                                         @DestinationVariable Long roomId,
                                         MessageRequest request) {
        User user = authUtil.resolveUser(userDetails);
        return messageService.createMessage(user, roomId, request);
    }

    /* 3. 삭제 */
    @MessageMapping("/community/discussions/messages/{id}")
    @SendTo("/topic/community/discussions/messages/{id}")
    public MessageDeleteResponse deleteMessage(@AuthenticationPrincipal UserDetails userDetails,
                                               @DestinationVariable Long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        messageService.deleteMessage(userId, id);
        return new MessageDeleteResponse(id);
    }

    /* 4. 좋아요 토글 */
    @MessageMapping("/community/discussions/messages/{id}/like")
    @SendTo("/topic/community/discussions/messages/{id}/like")
    public LikeResponse toggleLike(@AuthenticationPrincipal UserDetails userDetails,
                                   @DestinationVariable Long id) {
        Long userId = authUtil.resolveUserId(userDetails);
        return likeService.toggleLikeMessage(userId, id);
    }

    /* WebSocket 요청 예외 처리 */
    @MessageExceptionHandler({AccessDeniedException.class, MessageDeleteDeniedException.class})
    @SendToUser("/queue/errors")
    public ErrorMessage handleAccessDenied(RuntimeException e) {
        return new ErrorMessage(e.getMessage(), 403); // 403 Forbidden
    }

    @MessageExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    @SendToUser("/queue/errors")
    public ErrorMessage handleEntityNotFound(RuntimeException e) {
        return new ErrorMessage(e.getMessage(), 404); // 404 Not Found
    }
}
