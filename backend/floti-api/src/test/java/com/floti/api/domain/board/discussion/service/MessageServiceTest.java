package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.MessageRequest;
import com.floti.api.domain.board.discussion.dto.MessageResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import com.floti.api.domain.board.discussion.entity.Messages;
import com.floti.api.domain.board.discussion.repository.DiscussionRoomRepository;
import com.floti.api.domain.board.discussion.repository.MessageRepository;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private DiscussionRoomRepository discussionRoomRepository;

    @Mock
    private LikeService likeService;

    private static final Long VALID_ID = 1L;

    private final User testUser = User.builder().id(VALID_ID).nickname("테스터01").build();

    @Test
    @DisplayName("getMessages: 메시지 있음")
    void getMessages_exist() {
        //given
        List<Messages> messages = List.of(
                Messages.builder().id(VALID_ID).author(testUser).content("첫번째 메시지").build(),
                Messages.builder().id(VALID_ID + 1).author(testUser).content("두번째 메시지").build()
        );

        when(messageRepository.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(anyLong(), anyLong())).thenReturn(messages);
        when(likeService.getLikedMessageIds(anyLong(), anyList())).thenReturn(Collections.emptySet());

        //when
        List<MessageResponse> responses = messageService.getMessages(testUser.getId(), VALID_ID, VALID_ID);

        //then
        assertEquals(2, responses.size());
        assertEquals("첫번째 메시지", responses.get(0).getContent());
    }

    @Test
    @DisplayName("getMessages: 메시지 없음")
    void getMessages_empty() {
        //given
        when(messageRepository.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(likeService.getLikedMessageIds(anyLong(), eq(Collections.emptyList()))).thenReturn(Collections.emptySet());

        //when
        List<MessageResponse> responses = messageService.getMessages(testUser.getId(), VALID_ID, VALID_ID);

        //then
        assertTrue(responses.isEmpty());
    }

//    public MessageResponse createMessage(User user, Long roomId, MessageRequest request)
    @Test
    @DisplayName("createMessage: 메시지 등록")
    void createMessage_success() {
        //given
        DiscussionRooms discussionRoom = DiscussionRooms.builder().author(testUser).build();
        MessageRequest request = new MessageRequest();
        request.setContent("메시지 내용");

        when(discussionRoomRepository.findById(anyLong())).thenReturn(Optional.of(discussionRoom));
        when(messageRepository.save(any(Messages.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        MessageResponse responses = messageService.createMessage(testUser, VALID_ID, request);

        //then
        assertEquals("메시지 내용", responses.getContent());
        assertNotEquals(discussionRoom.getCreatedAt(), discussionRoom.getRecentActivityAt());
    }
}
