package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.MessageRequest;
import com.floti.api.domain.board.discussion.dto.MessageResponse;
import com.floti.api.domain.board.discussion.entity.Messages;
import com.floti.api.domain.board.discussion.repository.MessageRepository;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.exception.MessageDeleteDeniedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final LikeService likeService;

    /* 1. 조회 */
    public List<MessageResponse> getMessages(Long userId, Long roomId, Long last) {
        List<Messages> messages = messageRepository.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(roomId, last);
        List<Long> messageIds = messages.stream().map(Messages::getId).toList();
        Set<Long> likedMessageIds = likeService.getLikedMessageIds(userId, messageIds);

        return messages.stream()
                .map(m -> new MessageResponse(m, likedMessageIds.contains(m.getId())))
                .toList();
    }

    /* 2. 등록 */
    @Transactional
    public MessageResponse createMessage(User user, Long roomId, MessageRequest request) {
        Messages message = Messages.builder()
                .author(user)
                .roomId(roomId)
                .content(request.getContent())
                .build();

        return new MessageResponse(messageRepository.save(message), false);
    }

    /* 3. 삭제 */
    @Transactional
    public void deleteMessage(Long userId, Long id) {
        Messages message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MESSAGE_NOT_FOUND));

        if (!userId.equals(message.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        if (Duration.between(message.getCreatedAt(), LocalDateTime.now()).toMinutes() >= 5)
            throw new MessageDeleteDeniedException();

        messageRepository.delete(message);
    }
}
