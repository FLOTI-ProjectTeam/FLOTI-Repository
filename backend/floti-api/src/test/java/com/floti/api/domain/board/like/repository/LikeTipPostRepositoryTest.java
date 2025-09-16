package com.floti.api.domain.board.like.repository;

import com.floti.api.config.QuerydslTestConfig;
import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import com.floti.api.domain.board.tip.entity.TipPosts;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslTestConfig.class)
public class LikeTipPostRepositoryTest {
    @Autowired
    private LikeTipPostRepository likeTipPostRepository;

    @Autowired
    private EntityManager em;

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
        em.persist(testUser);

        testPost = TipPosts.builder()
                .author(testUser)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        em.persist(testPost);
    }

    @Test
    @DisplayName("existsByUserIdAndPostId: 추천 있음 - true 반환")
    void existsByUserIdAndPostId_true() {
        //given
        LikeTipPosts likeTipPost = new LikeTipPosts(testUser.getId(), testPost.getId());
        likeTipPostRepository.save(likeTipPost);

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
