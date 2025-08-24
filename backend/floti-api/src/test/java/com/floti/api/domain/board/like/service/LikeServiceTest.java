package com.floti.api.domain.board.like.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.entity.UserPostId;
import com.floti.api.domain.board.like.dto.LikeResponse;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import com.floti.api.domain.board.like.repository.LikeTipPostRepository;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeTipPostRepository likeTipPostRepository;

    @Mock
    private TipPostRepository tipPostRepository;

    @Mock
    private UserRepository userRepository;

    private static final Long VALID_ID = 1L;

    private final Users testUser = Users.builder().id(VALID_ID).nickname("테스터01").build();
    private final TipPosts testPost = TipPosts.builder().author(testUser).build();

    @Test
    @DisplayName("toggleLikeTipPost: 추천 없음 - 게시글 추천")
    void toggleLikeTipPost_like() {
        //given
        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(tipPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.findById(new UserPostId(VALID_ID, VALID_ID))).thenReturn(Optional.empty());
        when(likeTipPostRepository.save(any(LikeTipPosts.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        LikeResponse response = likeService.toggleLikeTipPost(VALID_ID, VALID_ID);

        //then
        assertEquals(1, response.getLikeCount());
        assertTrue(response.isLiked());
    }

    @Test
    @DisplayName("toggleLikeTipPost: 추천 있음 - 게시글 추천 취소")
    void toggleLikeTipPost_unlike() {
        //given
        LikeTipPosts likeTipPost = new LikeTipPosts(VALID_ID, VALID_ID);

        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        when(tipPostRepository.findById(VALID_ID)).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.findById(new UserPostId(VALID_ID, VALID_ID))).thenReturn(Optional.of(likeTipPost));
        doNothing().when(likeTipPostRepository).delete(any(LikeTipPosts.class));

        //when
        LikeResponse response = likeService.toggleLikeTipPost(VALID_ID, VALID_ID);

        //then
        assertEquals(0, response.getLikeCount());
        assertFalse(response.isLiked());
    }
}
