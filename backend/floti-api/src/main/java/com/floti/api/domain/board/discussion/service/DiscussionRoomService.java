package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomDetailResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomRequest;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomResponse;
import com.floti.api.domain.board.discussion.dto.MessageResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import com.floti.api.domain.board.discussion.entity.Messages;
import com.floti.api.domain.board.discussion.entity.RoomParticipantId;
import com.floti.api.domain.board.discussion.repository.DiscussionParticipantRepository;
import com.floti.api.domain.board.discussion.repository.DiscussionRoomRepository;
import com.floti.api.domain.board.discussion.repository.MessageRepository;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.exception.MaxParticipantExceededException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DiscussionRoomService {
    private final DiscussionRoomRepository discussionRoomRepository;
    private final MessageRepository messageRepository;
    private final DiscussionParticipantRepository discussionParticipantRepository;
    private final LikeService likeService;

    private static final int PAGE_SIZE = 20;

    /* 1-1. 조회 */
    public Page<DiscussionRoomResponse> getDiscussionRooms(String sort, int page) {
        Sort.Order baseOrder = Sort.Order.desc("id");
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "latest" -> Sort.by(baseOrder);
            case "registered" -> Sort.by("id").ascending();
            default -> Sort.by(Sort.Order.desc("recentActivityAt"), baseOrder);
        };

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sortOrder);
        Page<DiscussionRooms> discussionRoomPage = discussionRoomRepository.findAll(pageable);
        return discussionRoomPage.map(DiscussionRoomResponse::new);
    }

    /* 1-2. 검색 */
    public Page<DiscussionRoomResponse> searchDiscussionRooms(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<DiscussionRooms> discussionRoomPage = discussionRoomRepository.searchDiscussionRooms(search, sort, pageable);
        return discussionRoomPage.map(DiscussionRoomResponse::new);
    }

    /* 2. 상세 조회 */
    public DiscussionRoomDetailResponse getDiscussionRoom(Long userId, Long id) {
        DiscussionRooms discussionRoom = discussionRoomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ROOM_NOT_FOUND));

        /* 참여자 리스트 */
        List<DiscussionParticipants> discussionParticipants = discussionParticipantRepository.findByRoomId(id);
        List<AuthorResponse> participantResponses = discussionParticipants.stream()
                .map(dp -> new AuthorResponse(dp.getParticipant()))
                .toList();

        /* 메시지별 추천 여부 */
        // 1. 모든 메시지 ID
        List<Messages> messages = messageRepository.findTop50ByRoomIdOrderByIdDesc(id);
        List<Long> messageIds = messages.stream().map(Messages::getId).toList();

        // 2. 사용자가 추천한 메시지 ID
        Set<Long> likedMessageIds = likeService.getLikedMessageIds(userId, messageIds);

        List<MessageResponse> messageResponses = messages.stream()
                .map(m -> new MessageResponse(m, likedMessageIds.contains(m.getId())))
                .toList();

        return new DiscussionRoomDetailResponse(discussionRoom, participantResponses, messageResponses);
    }

    /* 3. 등록 */
    @Transactional
    public DiscussionRoomResponse createDiscussionRoom(User user, DiscussionRoomRequest request) {
        DiscussionRooms DiscussionRoom = DiscussionRooms.builder()
                .author(user)
                .title(request.getTitle())
                .intro(request.getIntro())
                .build();

        return new DiscussionRoomResponse(discussionRoomRepository.save(DiscussionRoom));
    }

    /* 4. 수정 */
    @Transactional
    public DiscussionRoomResponse updateDiscussionRoom(Long userId, Long id, DiscussionRoomRequest request) {
        DiscussionRooms discussionRoom = discussionRoomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ROOM_NOT_FOUND));

        if (!userId.equals(discussionRoom.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        discussionRoom.update(request.getTitle(), request.getIntro(), request.getMaxParticipants());
        return new DiscussionRoomResponse(discussionRoom);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteDiscussionRoom(Long userId, Long id) {
        DiscussionRooms discussionRoom = discussionRoomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ROOM_NOT_FOUND));

        if (!userId.equals(discussionRoom.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        discussionRoomRepository.delete(discussionRoom);
    }

    /* 6. 참여 토글 */
    @Transactional
    public void toggleJoinDiscussion(User user, Long roomId) {
        DiscussionRooms discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ROOM_NOT_FOUND));
        Long userId = user.getId();

        if (discussionRoom.getAuthor().getId().equals(userId))
            throw new AccessDeniedException(ExceptionMessage.HOST_CANNOT_LEAVE);

        Optional<DiscussionParticipants> discussionParticipant =
                discussionParticipantRepository.findById(new RoomParticipantId(roomId, userId));

        if (discussionParticipant.isPresent()) {
            discussionParticipantRepository.delete(discussionParticipant.get());
            discussionRoom.decrementParticipantCount();
        } else {
            if (discussionRoom.getParticipantCount() == discussionRoom.getMaxParticipants())
                throw new MaxParticipantExceededException();

            discussionParticipantRepository.save(new DiscussionParticipants(discussionRoom, user));
            discussionRoom.incrementParticipantCount();
        }
    }
}
