package com.floti.api.domain.like.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.like.dto.LikeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") //application-test.yml 사용
@Transactional
public class LikeServiceTest {
    @Autowired
    private LikeService likeService;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testPostId;

    @BeforeEach //테스트용 데이터 생성 및 저장
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
    @DisplayName("toggleLikeTipPost: 게시글 추천 추가")
    void toggleLikeTipPost_add() {
        LikeResponse response = likeService.toggleLikeTipPost(testUserId, testPostId);

        assertEquals(1, response.getLikeCount());
        assertTrue(response.isLiked());
    }

    @Test
    @DisplayName("toggleLikeTipPost: 게시글 추천 취소")
    void toggleLikeTipPost_cancel() {
        likeService.toggleLikeTipPost(testUserId, testPostId);
        LikeResponse response = likeService.toggleLikeTipPost(testUserId, testPostId);

        assertEquals(0, response.getLikeCount());
        assertFalse(response.isLiked());
    }
}
