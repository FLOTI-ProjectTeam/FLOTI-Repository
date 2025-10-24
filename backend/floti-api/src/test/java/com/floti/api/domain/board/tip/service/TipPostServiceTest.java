package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.like.repository.LikeTipPostRepository;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TipPostServiceTest {
    @InjectMocks
    private TipPostService tipPostService;

    @Mock
    private TipPostRepository tipPostRepository;

    @Mock
    private LikeTipPostRepository likeTipPostRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final User testUser = User.builder().id(VALID_ID).nickname("테스터01").build();
    private final TipPosts testPost = TipPosts.builder()
            .author(testUser).title("테스트 제목").content("테스트 내용").build();

    @Test
    @DisplayName("getTipPosts: 게시글 있음 - 게시글 페이지 반환")
    void getTipPosts_exist() {
        //given
        Page<TipPosts> page = new PageImpl<>(List.of(testPost));

        when(tipPostRepository.findAll(any(Pageable.class))).thenReturn(page);

        //when
        Page<TipPostResponse> responses = tipPostService.getTipPosts("latest", 0);

        //then
        assertEquals(1, responses.getTotalElements());
        assertEquals("테스트 제목", responses.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("getTipPosts: 게시글 없음 - 빈 페이지 반환")
    void getTipPosts_empty() {
        //given
        when(tipPostRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        //when
        Page<TipPostResponse> responses = tipPostService.getTipPosts("latest", 0);

        //then
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("getTipPost: 추천 없음 - 게시글 상세 반환 (liked=false)")
    void getTipPost_likedFalse() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        //when
        TipPostResponse response = tipPostService.getTipPost(VALID_ID, VALID_ID);

        //then
        assertFalse(response.isLiked());
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스트 내용", response.getContent());
    }

    @Test
    @DisplayName("getTipPost: 추천 있음 - 게시글 상세 반환 (liked=true)")
    void getTipPost_likedTrue() {
        // given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(true);

        // when
        TipPostResponse response = tipPostService.getTipPost(VALID_ID, VALID_ID);

        // then
        assertTrue(response.isLiked());
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스트 내용", response.getContent());
    }

    @Test
    @DisplayName("getTipPost: 게시글 없음 - EntityNotFoundException")
    void getTipPost_fail_postNotFound() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tipPostService.getTipPost(VALID_ID, INVALID_ID);
        });

        //then
        assertEquals(ExceptionMessage.POST_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 등록")
    void createTipPost_success() {
        //given
        PostRequest request = new PostRequest();
        request.setTitle("등록된 제목");
        request.setContent("등록된 내용");

        when(tipPostRepository.save(any(TipPosts.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        TipPostResponse response = tipPostService.createTipPost(testUser, request, null);

        //then
        assertEquals("등록된 제목", response.getTitle());
        assertEquals("등록된 내용", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
    }

    @Test
    @DisplayName("updateTipPost: 게시글 수정")
    void updateTipPost_success() {
        //given
        PostRequest request = new PostRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        TipPostResponse response = tipPostService.updateTipPost(testUser, VALID_ID, request, null, false);

        //then
        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("updateTipPost: 작성자 아님 - AccessDeniedException")
    void updateTipPost_fail_authorMismatch() {
        //given
        User user = User.builder().id(INVALID_ID).nickname("테스터02").build();
        PostRequest request = new PostRequest();

        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.updateTipPost(user, VALID_ID, request, null, false);
        });

        //then
        assertEquals(ExceptionMessage.UPDATE_DENIED, exception.getMessage());
    }

    @Test
    @DisplayName("deleteTipPost: 게시글 삭제")
    void deleteTipPost_success() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        tipPostService.deleteTipPost(VALID_ID, VALID_ID);

        //then
        verify(tipPostRepository).delete(testPost);
    }

    @Test
    @DisplayName("deleteTipPost: 작성자 아님 - AccessDeniedException")
    void deleteTipPost_fail_authorMismatch() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.deleteTipPost(INVALID_ID, VALID_ID);
        });

        //then
        assertEquals(ExceptionMessage.DELETE_DENIED, exception.getMessage());
    }
}
