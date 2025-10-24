package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.dto.DiscussionPostDetailResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import com.floti.api.domain.board.discussion.entity.Messages;
import com.floti.api.domain.board.discussion.entity.PostParticipantId;
import com.floti.api.domain.board.discussion.repository.DiscussionParticipantRepository;
import com.floti.api.domain.board.discussion.repository.DiscussionPostRepository;
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
public class DiscussionPostServiceTest {
    @InjectMocks
    private DiscussionPostService discussionPostService;

    @Mock
    private LikeService likeService;

    @Mock
    private DiscussionPostRepository discussionPostRepository;

    @Mock
    private DiscussionParticipantRepository discussionParticipantRepository;

    @Mock
    private MessageRepository messageRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final User testUser = User.builder().id(VALID_ID).nickname("테스터01").build();
    private final User testParticipant = User.builder().id(INVALID_ID).nickname("테스터02").build();
    private final DiscussionPosts testPost = DiscussionPosts.builder().author(testUser).title("테스트 제목").maxParticipants(2).build();

    @Test
    @DisplayName("getDiscussionPost: 참여자와 메시지 있음 - 게시글 상세에 참여자와 메시지 리스트 포함")
    void getDiscussionPost_exist() {
        //given
        List<Messages> messages = List.of(
                Messages.builder().id(VALID_ID).author(testUser).content("첫번째 메시지").build(),
                Messages.builder().id(VALID_ID).author(testUser).content("두번째 메시지").build(),
                Messages.builder().id(INVALID_ID).author(testParticipant).content("세번째 메시지").build()
        );

        testPost.incrementParticipantCount();

        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(discussionParticipantRepository.findByPostId(anyLong()))
                .thenReturn(List.of(new DiscussionParticipants(testPost, testParticipant)));
        when(messageRepository.findTop50ByPostIdOrderByIdDesc(anyLong())).thenReturn(messages);
        when(likeService.getLikedMessageIds(VALID_ID, List.of(VALID_ID, VALID_ID, INVALID_ID)))
                .thenReturn(Set.of(VALID_ID));

        //when
        DiscussionPostDetailResponse response = discussionPostService.getDiscussionPost(VALID_ID, VALID_ID);

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
    @DisplayName("getDiscussionPost: 참여자와 메시지 없음 - 게시글 상세에 빈 리스트 포함")
    void getDiscussionPost_empty() {
        //given
        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(discussionParticipantRepository.findByPostId(anyLong())).thenReturn(Collections.emptyList());
        when(messageRepository.findTop50ByPostIdOrderByIdDesc(anyLong())).thenReturn(Collections.emptyList());
        when(likeService.getLikedMessageIds(VALID_ID, Collections.emptyList())).thenReturn(Collections.emptySet());

        //when
        DiscussionPostDetailResponse response = discussionPostService.getDiscussionPost(VALID_ID, VALID_ID);

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
        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(discussionParticipantRepository.findById(any(PostParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        discussionPostService.toggleJoinDiscussion(testParticipant, VALID_ID);

        //then
        assertEquals(2, testPost.getParticipantCount());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 토론 참여 인원 초과 - MaxParticipantExceededException")
    void toggleJoinDiscussion_fail_maxParticipantExceeded() {
        //given
        User participant = User.builder().id(INVALID_ID).nickname("테스터03").build();

        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(discussionParticipantRepository.findById(any(PostParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        discussionPostService.toggleJoinDiscussion(testParticipant, VALID_ID);

        //when
        MaxParticipantExceededException exception = assertThrows(MaxParticipantExceededException.class, () -> {
            discussionPostService.toggleJoinDiscussion(participant, VALID_ID);
        });

        //then
        assertEquals("최대 참여 인원을 초과했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 토론 참여 취소")
    void toggleJoinDiscussion_leave() {
        //given
        DiscussionParticipants discussionParticipant = new DiscussionParticipants(testPost, testUser);

        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(discussionParticipantRepository.findById(any(PostParticipantId.class))).thenReturn(Optional.empty());
        when(discussionParticipantRepository.save(any(DiscussionParticipants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        discussionPostService.toggleJoinDiscussion(testParticipant, INVALID_ID);

        when(discussionParticipantRepository.findById(any(PostParticipantId.class))).thenReturn(Optional.of(discussionParticipant));

        //when
        discussionPostService.toggleJoinDiscussion(testParticipant, INVALID_ID);

        //then
        verify(discussionParticipantRepository).delete(discussionParticipant);
        assertEquals(1, testPost.getParticipantCount());
    }

    @Test
    @DisplayName("toggleJoinDiscussion: 주최자 토론 참여 쥐소 - AccessDeniedException")
    void toggleJoinDiscussion_fail_hostLeave() {
        //given
        when(discussionPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            discussionPostService.toggleJoinDiscussion(testUser, VALID_ID);
        });

        //then
        assertEquals(ExceptionMessage.HOST_CANNOT_LEAVE, exception.getMessage());
    }
}
