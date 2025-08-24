package com.floti.api.domain.board.like.repository;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
public class LikeTipPostRepositoryTest {
    @Autowired
    private LikeTipPostRepository likeTipPostRepository;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;
    private TipPosts testPost;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        userRepository.save(testUser);

        testPost = TipPosts.builder()
                .author(testUser)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        tipPostRepository.save(testPost);
    }

    @Test
    @DisplayName("existsByUserIdAndPostId: 추천 있음 - true 반환")
    void existsByUserIdAndPostId_true() {
        //given
        LikeTipPosts likeTipPosts = new LikeTipPosts(testUser.getId(), testPost.getId());
        likeTipPostRepository.save(likeTipPosts);

        //when
        boolean result = likeTipPostRepository.existsByUserIdAndPostId(testUser.getId(), testPost.getId());

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("existsByUserIdAndPostId: 추천 없음 - false 반환")
    void existsByUserIdAndPostId_false() {
        //when
        boolean result = likeTipPostRepository.existsByUserIdAndPostId(testUser.getId(), testPost.getId());

        //then
        assertFalse(result);
    }
}
