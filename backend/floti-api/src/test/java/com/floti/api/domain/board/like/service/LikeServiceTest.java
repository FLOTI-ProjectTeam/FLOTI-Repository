package com.floti.api.domain.board.like.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.entity.LikeTipPosts;
import com.floti.api.domain.like.entity.UserPostId;
import com.floti.api.domain.like.repository.LikeTipPostRepository;
import com.floti.api.domain.like.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private static final Long VALID_ID = 1L;

    private final User testUser = User.builder().id(VALID_ID).nickname("테스터01").build();
    private final TipPosts testPost = TipPosts.builder().author(testUser).build();

    @Test
    @DisplayName("toggleLikeTipPost: 게시글 추천")
    void toggleLikeTipPost_like() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
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
    @DisplayName("toggleLikeTipPost: 게시글 추천 취소")
    void toggleLikeTipPost_unlike() {
        //given
        LikeTipPosts likeTipPost = new LikeTipPosts(VALID_ID, VALID_ID);

        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.findById(new UserPostId(VALID_ID, VALID_ID))).thenReturn(Optional.of(likeTipPost));
        doNothing().when(likeTipPostRepository).delete(any(LikeTipPosts.class));

        //when
        LikeResponse response = likeService.toggleLikeTipPost(VALID_ID, VALID_ID);

        //then
        assertEquals(0, response.getLikeCount());
        assertFalse(response.isLiked());
    }
}
