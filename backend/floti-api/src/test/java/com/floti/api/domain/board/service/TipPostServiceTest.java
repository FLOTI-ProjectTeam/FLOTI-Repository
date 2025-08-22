package com.floti.api.domain.board.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.dto.PostRequest;
import com.floti.api.domain.board.tip.dto.TipPostResponse;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.board.tip.service.TipPostService;
import com.floti.api.error.PostNotFoundException;
import com.floti.api.error.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") //application-test.yml 사용
@Transactional
public class TipPostServiceTest {
    @Autowired
    private TipPostService tipPostService;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testPostId;

    @BeforeEach //테스트용 데이터 생성
    void setUp() {
        Users user = Users.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        userRepository.save(user);
        testUserId = user.getId();

        TipPosts tipPost = TipPosts.builder()
                .author(userRepository.findById(testUserId).get())
                .title("원본 제목")
                .content("원본 내용")
                .build();
        tipPostRepository.save(tipPost);
        testPostId = tipPost.getId();
    }

    @Test
    @DisplayName("getTipPosts: 게시글 조회")
    void getTipPosts_withoutSearch() {
        String search = "";
        Pageable pageable = PageRequest.of(0, 20);

        TipPosts tipPost = TipPosts.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .author(userRepository.findById(testUserId).get())
                .build();
        tipPostRepository.save(tipPost);

        Page<TipPostResponse> responses = tipPostService.getTipPosts(search, pageable);

        assertEquals(2, responses.getTotalElements());
        assertEquals("테스트 제목", responses.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("getTipPosts: 게시글 검색")
    void getTipPosts_withSearch() {
        String search = "테스트";
        Pageable pageable = PageRequest.of(0, 20);

        TipPosts tipPost = TipPosts.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .author(userRepository.findById(testUserId).get())
                .build();
        tipPostRepository.save(tipPost);

        Page<TipPostResponse> responses = tipPostService.getTipPosts(search, pageable);

        assertEquals(1, responses.getTotalElements());
        assertEquals("테스트 제목", responses.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("getTipPost: 게시글 상세 조회")
    void getTipPost_success() {
        TipPostResponse response = tipPostService.getTipPost(testUserId, testPostId);

        assertEquals("원본 제목", response.getTitle());
        assertEquals("원본 내용", response.getContent());
        assertFalse(response.isLiked());
    }

    @Test
    @DisplayName("getTipPost: 게시글 상세 조회 [게시글 없음]")
    void getTipPost_fail_postNotFound() {
        Long invalidPostId = 9999L;

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            tipPostService.getTipPost(testUserId, invalidPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 등록")
    void createTipPost_success() {
        PostRequest request = new PostRequest();
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        TipPostResponse response = tipPostService.createTipPost(testUserId, request, null);

        assertNotNull(response.getId());
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스터01", response.getAuthor().getNickname());
    }

    @Test
    @DisplayName("createTipPost: 게시글 등록 [사용자 없음]")
    void createTipPost_fail_userNotFound() {
        PostRequest request = new PostRequest();
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        Long invalidUserId = 9999L;

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tipPostService.createTipPost(invalidUserId, request, null);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateTipPost: 게시글 수정")
    void updateTipPost_success() {
        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("수정된 제목");
        updateRequest.setContent("수정된 내용");

        TipPostResponse response = tipPostService.updateTipPost(testUserId, testPostId, updateRequest);

        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("updateTipPost: 게시글 수정 [작성자 불일치]")
    void updateTipPost_fail_authorMismatch() {
        Users user = Users.builder()
                .email("test02@gmail.com")
                .username("test02")
                .password("password123")
                .nickname("테스터02")
                .build();
        userRepository.save(user);

        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("변경된 제목");
        updateRequest.setContent("변경된 내용");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.updateTipPost(user.getId(), testPostId, updateRequest);
        });

        assertEquals("게시글을 수정할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteTipPost: 게시글 삭제")
    void deleteTipPost_success() {
        tipPostService.deleteTipPost(testUserId, testPostId);

        assertFalse(tipPostRepository.findById(testPostId).isPresent());
    }

    @Test
    @DisplayName("deleteTipPost: 게시글 삭제 [작성자 불일치]")
    void deleteTipPost_fail_authorMismatch() {
        Users user = Users.builder()
                .email("test02@gmail.com")
                .username("test02")
                .password("password123")
                .nickname("테스터02")
                .build();
        userRepository.save(user);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.deleteTipPost(user.getId(), testPostId);
        });

        assertEquals("게시글을 삭제할 권한이 없습니다.", exception.getMessage());
    }
}
