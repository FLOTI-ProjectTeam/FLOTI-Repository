package com.floti.api.domain.board.service;

import com.floti.api.domain.board.dto.TipPostCreateRequest;
import com.floti.api.domain.board.dto.TipPostResponse;
import com.floti.api.domain.board.dto.TipPostUpdateRequest;
import com.floti.api.domain.board.entity.TipPosts;
import com.floti.api.domain.board.repository.TipPostRepository;
import com.floti.api.temp.entity.Users;
import com.floti.api.temp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

    @BeforeEach // 테스트용 데이터 생성 및 저장
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
    @DisplayName("createTipPost: 게시글 조회")
    void getTipPosts_withoutSearch_returnsAll() {
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
    @DisplayName("createTipPost: 게시글 검색")
    void getTipPosts_withSearch_returnsFilteredPage() {
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
    @DisplayName("createTipPost: 게시글 상세 조회")
    void getTipPost_success() {
        TipPostResponse response = tipPostService.getTipPost(testPostId);

        assertEquals("원본 제목", response.getTitle());
        assertEquals("원본 내용", response.getContent());
    }

    @Test
    @DisplayName("createTipPost: 게시글 상세 조회 실패")
    void getTipPost_fail_postNotFound() {
        Long invalidPostId = 9999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.getTipPost(invalidPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 등록")
    void createTipPost_success() {
        TipPostCreateRequest request = new TipPostCreateRequest();
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        TipPostResponse response = tipPostService.createTipPost(testUserId, request, null);

        assertNotNull(response.getId());
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스터01", response.getAuthorNickname());
    }

    @Test
    @DisplayName("createTipPost: 게시글 등록 [사용자 없음]")
    void createTipPost_fail_userNotFound() {
        TipPostCreateRequest request = new TipPostCreateRequest();
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        Long invalidUserId = 9999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.createTipPost(invalidUserId, request, null);
        });

        assertEquals("사용자 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 수정")
    void updateTipPost_success() {
        TipPostUpdateRequest updateRequest = new TipPostUpdateRequest();
        updateRequest.setId(testPostId);
        updateRequest.setTitle("수정된 제목");
        updateRequest.setContent("수정된 내용");

        TipPostResponse response = tipPostService.updateTipPost(testUserId, updateRequest);

        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("createTipPost: 게시글 수정 실패 [게시글 없음]")
    void updateTipPost_fail_postNotFound() {
        TipPostUpdateRequest updateRequest = new TipPostUpdateRequest();
        updateRequest.setId(9999L);
        updateRequest.setTitle("변경된 제목");
        updateRequest.setContent("변경된 내용");

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.updateTipPost(testUserId, updateRequest);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 수정 실패 [사용자 없음]")
    void updateTipPost_fail_userNotFound() {
        TipPostUpdateRequest updateRequest = new TipPostUpdateRequest();
        updateRequest.setId(testPostId);
        updateRequest.setTitle("변경된 제목");
        updateRequest.setContent("변경된 내용");

        Long invalidUserId = 9999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.updateTipPost(invalidUserId, updateRequest);
        });

        assertEquals("사용자 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 수정 실패 [작성자 불일치]")
    void updateTipPost_fail_authorMismatch() {
        Users user = Users.builder()
                .email("test02@gmail.com")
                .username("test02")
                .password("password123")
                .nickname("테스터02")
                .build();
        userRepository.save(user);

        TipPostUpdateRequest updateRequest = new TipPostUpdateRequest();
        updateRequest.setId(testPostId);
        updateRequest.setTitle("변경된 제목");
        updateRequest.setContent("변경된 내용");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            tipPostService.updateTipPost(user.getId(), updateRequest);
        });

        assertEquals("게시글을 수정할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 삭제")
    void deleteTipPost_success() {
        tipPostService.deleteTipPost(testUserId, testPostId);

        assertFalse(tipPostRepository.findById(testPostId).isPresent());
    }

    @Test
    @DisplayName("createTipPost: 게시글 삭제 실패 [게시글 없음]")
    void deleteTipPost_fail_postNotFound() {
        Long invalidPostId = 9999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.deleteTipPost(testUserId, invalidPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 삭제 실패 [사용자 없음]")
    void deleteTipPost_fail_userNotFound() {
        Long invalidUserId = 9999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tipPostService.deleteTipPost(invalidUserId, testPostId);
        });

        assertEquals("사용자 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createTipPost: 게시글 삭제 실패 [작성자 불일치]")
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
