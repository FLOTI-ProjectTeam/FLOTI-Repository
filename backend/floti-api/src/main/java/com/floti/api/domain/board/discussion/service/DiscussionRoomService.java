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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionRoomService {
    private final DiscussionRoomRepository discussionRoomRepository;
    private final MessageRepository messageRepository;
    private final DiscussionParticipantRepository discussionParticipantRepository;
    private final LikeService likeService;

    private static final int PAGE_SIZE = 20;

    /* 사용자의 참여 여부 포함 */
    private Page<DiscussionRoomResponse> mapWithJoined(Page<DiscussionRooms> roomPage, Long userId) {
        Set<Long> joinedRoomIds = new HashSet<>(discussionParticipantRepository.findJoinedRoomIdsByUserId(userId));
        return roomPage.map(room -> new DiscussionRoomResponse(room, joinedRoomIds.contains(room.getId())));
    }

    /* 1-1. 조회 */
    public Page<DiscussionRoomResponse> getDiscussionRooms(Long userId, String sort, int page) {
        Sort.Order baseOrder = Sort.Order.desc("id");
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "latest" -> Sort.by(baseOrder);
            case "registered" -> Sort.by("id").ascending();
            default -> Sort.by(Sort.Order.desc("recentActivityAt"), baseOrder);
        };

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sortOrder);
        Page<DiscussionRooms> discussionRoomPage = discussionRoomRepository.findAll(pageable);
        return mapWithJoined(discussionRoomPage, userId);
    }

    /* 1-2. 검색 */
    public Page<DiscussionRoomResponse> searchDiscussionRooms(Long userId, String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<DiscussionRooms> discussionRoomPage = discussionRoomRepository.searchDiscussionRooms(search, sort, pageable);
        return mapWithJoined(discussionRoomPage, userId);
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

        /* 메시지별 좋아요 여부 */
        // 1. 모든 메시지 ID
        List<Messages> messages = messageRepository.findTop50ByRoomIdOrderByIdDesc(id);
        List<Long> messageIds = messages.stream().map(Messages::getId).toList();

        // 2. 사용자가 좋아요한 메시지 ID
        Set<Long> likedMessageIds = likeService.getLikedMessageIds(userId, messageIds);

        List<MessageResponse> messageResponses = messages.stream()
                .map(m -> new MessageResponse(m, likedMessageIds.contains(m.getId())))
                .toList();

        return new DiscussionRoomDetailResponse(discussionRoom, participantResponses, messageResponses);
    }

    /* 3. 등록 */
    @Transactional
    public DiscussionRoomResponse createDiscussionRoom(User user, DiscussionRoomRequest request) {
        DiscussionRooms discussionRoom = DiscussionRooms.builder()
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        discussionRoomRepository.save(discussionRoom);
        discussionParticipantRepository.save(new DiscussionParticipants(discussionRoom, user));

        return new DiscussionRoomResponse(discussionRoom);
    }

    /* 4. 수정 */
    @Transactional
    public DiscussionRoomResponse updateDiscussionRoom(Long userId, Long id, DiscussionRoomRequest request) {
        DiscussionRooms discussionRoom = discussionRoomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ROOM_NOT_FOUND));

        if (!userId.equals(discussionRoom.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        discussionRoom.update(request.getTitle(), request.getContent(), request.getMaxParticipantCount());
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
            if (discussionRoom.getParticipantCount() == discussionRoom.getMaxParticipantCount())
                throw new MaxParticipantExceededException();

            discussionParticipantRepository.save(new DiscussionParticipants(discussionRoom, user));
            discussionRoom.incrementParticipantCount();
        }
    }
}
