package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.DiscussionRoomDetailResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiscussionRoomServiceTest {
    @InjectMocks
    private DiscussionRoomService discussionRoomService;

    @Mock
    private LikeService likeService;

    @Mock
    private DiscussionRoomRepository discussionRoomRepository;

    @Mock
    private DiscussionParticipantRepository discussionParticipantRepository;

    @Mock
    private MessageRepository messageRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final User testUser = User.builder().id(VALID_ID).nickname("테스터01").build();
    private final User testParticipant = User.builder().id(INVALID_ID).nickname("테스터02").build();
    private final DiscussionRooms testRoom = DiscussionRooms.builder().author(testUser).title("테스트 제목").maxParticipants(2).build();

    @Test
    @DisplayName("getDiscussionRoom: 참여자와 메시지 있음 - 게시글 상세에 참여자와 메시지 리스트 포함")
    void getDiscussionRoom_exist() {
        //given
        List<Messages> messages = List.of(
                Messages.builder().id(VALID_ID).author(testUser).content("첫번째 메시지").build(),
                Messages.builder().id(VALID_ID).author(testUser).content("두번째 메시지").build(),
                Messages.builder().id(INVALID_ID).author(testParticipant).content("세번째 메시지").build()
        );

        testRoom.incrementParticipantCount();

        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
        when(discussionParticipantRepository.findByRoomId(anyLong()))
                .thenReturn(List.of(new DiscussionParticipants(testRoom, testParticipant)));
        when(messageRepository.findTop50ByRoomIdOrderByIdDesc(anyLong())).thenReturn(messages);
        when(likeService.getLikedMessageIds(VALID_ID, List.of(VALID_ID, VALID_ID, INVALID_ID)))
                .thenReturn(Set.of(VALID_ID));

        //when
        DiscussionRoomDetailResponse response = discussionRoomService.getDiscussionRoom(VALID_ID, VALID_ID);

        //then
        assertEquals("테스트 제목", response.getTitle());
        assertEquals(2, response.getParticipantCount());
        assertEquals(1, response.getParticipants().size());
        assertEquals(3, response.getMessages().size());
        assertEquals("테스터02", response.getParticipants().get(0).getNickname());
        assertEquals("첫번째 메시지", response.getMessages().get(0).getContent());
        assertTrue(response.getMessages().get(0).isLiked());
        assertFalse(response.getMessages().get(2).isLiked());
    }

    @Test
    @DisplayName("getDiscussionRoom: 참여자와 메시지 없음 - 게시글 상세에 빈 리스트 포함")
    void getDiscussionRoom_empty() {
        //given
        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
        when(discussionParticipantRepository.findByRoomId(anyLong())).thenReturn(Collections.emptyList());
        when(messageRepository.findTop50ByRoomIdOrderByIdDesc(anyLong())).thenReturn(Collections.emptyList());
        when(likeService.getLikedMessageIds(VALID_ID, Collections.emptyList())).thenReturn(Collections.emptySet());

        //when
        DiscussionRoomDetailResponse response = discussionRoomService.getDiscussionRoom(VALID_ID, VALID_ID);

        //then
        assertEquals("테스트 제목", response.getTitle());
        assertEquals(1, response.getParticipantCount());
        assertTrue(response.getParticipants().isEmpty());
        assertTrue(response.getMessages().isEmpty());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 토론 참여")
    void toggleJoinDiscussion_join() {
        //given
        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
        when(discussionParticipantRepository.findById(any(RoomParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        discussionRoomService.toggleJoinDiscussion(testParticipant, VALID_ID);

        //then
        assertEquals(2, testRoom.getParticipantCount());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 토론 참여 인원 초과 - MaxParticipantExceededException")
    void toggleJoinDiscussion_fail_maxParticipantExceeded() {
        //given
        User participant = User.builder().id(INVALID_ID).nickname("테스터03").build();

        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
        when(discussionParticipantRepository.findById(any(RoomParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        discussionRoomService.toggleJoinDiscussion(testParticipant, VALID_ID);

        //when
        MaxParticipantExceededException exception = assertThrows(MaxParticipantExceededException.class, () -> {
            discussionRoomService.toggleJoinDiscussion(participant, VALID_ID);
        });

        //then
        assertEquals("최대 참여 인원을 초과했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 토론 참여 취소")
    void toggleJoinDiscussion_leave() {
        //given
        DiscussionParticipants discussionParticipant = new DiscussionParticipants(testRoom, testUser);

        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
        when(discussionParticipantRepository.findById(any(RoomParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        discussionRoomService.toggleJoinDiscussion(testParticipant, INVALID_ID);

        when(discussionParticipantRepository.findById(any(RoomParticipantId.class))).thenReturn(Optional.of(discussionParticipant));

        //when
        discussionRoomService.toggleJoinDiscussion(testParticipant, INVALID_ID);

        //then
        verify(discussionParticipantRepository).delete(discussionParticipant);
        assertEquals(1, testRoom.getParticipantCount());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 주최자 토론 참여 쥐소 - AccessDeniedException")
    void toggleJoinDiscussion_fail_hostLeave() {
        //given
        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            discussionRoomService.toggleJoinDiscussion(testUser, VALID_ID);
        });

        //then
        assertEquals(ExceptionMessage.HOST_CANNOT_LEAVE, exception.getMessage());
    }
}
