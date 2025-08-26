package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.like.repository.LikeTipPostRepository;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.exception.PostNotFoundException;
import com.floti.api.error.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Mock
    private UserRepository userRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final Users testUser = Users.builder().id(VALID_ID).nickname("테스터01").build();
    private final TipPosts testPost = TipPosts.builder()
            .author(testUser).title("테스트 제목").content("테스트 내용").build();

    @Test
    @DisplayName("getTipPosts: 검색어 없음 - 전체 게시글 조회")
    void getTipPosts_noSearch() {
        //given
        Pageable pageable = PageRequest.of(0, 20);
        Page<TipPosts> page = new PageImpl<>(List.of(testPost));

        when(tipPostRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<TipPostResponse> responses = tipPostService.getTipPosts("", pageable);

        //then
        assertEquals(1, responses.getTotalElements());
        assertEquals("테스트 제목", responses.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("getTipPosts: 검색어 있음 - 게시글 조회")
    void getTipPosts_search() {
        //given
        Pageable pageable = PageRequest.of(0, 20);
        Page<TipPosts> page = new PageImpl<>(List.of(testPost));

        when(tipPostRepository.findByTitleContainingIgnoreCase(pageable, "테스트")).thenReturn(page);

        //when
        Page<TipPostResponse> responses = tipPostService.getTipPosts("테스트", pageable);

        //then
        assertEquals(1, responses.getTotalElements());
        assertEquals("테스트 제목", responses.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("getTipPost: 게시글 있음 - 게시글 상세 반환")
    void getTipPost_success() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(likeTipPostRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        //when
        TipPostResponse response = tipPostService.getTipPost(VALID_ID, VALID_ID);

        //then
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스트 내용", response.getContent());
    }

    @Test
    @DisplayName("getTipPost: 게시글 없음 - PostNotFoundException")
    void getTipPost_fail_postNotFound() {
        //given
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            tipPostService.getTipPost(VALID_ID, INVALID_ID);
        });

        //then
        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 사용자 있음 - 게시글 등록")
    void createTipPost_success() {
        //given
        PostRequest request = new PostRequest();
        request.setTitle("등록된 제목");
        request.setContent("등록된 내용");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.save(any(TipPosts.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        TipPostResponse response = tipPostService.createTipPost(VALID_ID, request, null);

        //then
        assertEquals("등록된 제목", response.getTitle());
        assertEquals("등록된 내용", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
    }

    @Test
    @DisplayName("createTipPost: 사용자 없음 - UserNotFoundException")
    void createTipPost_fail_userNotFound() {
        //given
        PostRequest request = new PostRequest();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tipPostService.createTipPost(INVALID_ID, request, null);
        });

        //then
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateTipPost: 작성자 맞음 - 게시글 수정")
    void updateTipPost_success() {
        //given
        PostRequest request = new PostRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        TipPostResponse response = tipPostService.updateTipPost(VALID_ID, VALID_ID, request);

        //then
        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("updateTipPost: 작성자 아님 - AccessDeniedException")
    void updateTipPost_fail_authorMismatch() {
        //given
        PostRequest request = new PostRequest();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.updateTipPost(INVALID_ID, VALID_ID, request);
        });

        //then
        assertEquals("수정할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteTipPost: 작성자 맞음 - 게시글 삭제")
    void deleteTipPost_success() {
        //given
        when(userRepository.existsById(anyLong())).thenReturn(true);
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
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        //when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.deleteTipPost(INVALID_ID, VALID_ID);
        });

        //then
        assertEquals("삭제할 권한이 없습니다.", exception.getMessage());
    }
}
