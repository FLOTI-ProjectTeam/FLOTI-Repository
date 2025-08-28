package com.floti.api.domain.board.like.repository;

import com.floti.api.config.QuerydslTestConfig;
import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.board.like.entity.LikeComments;
import com.floti.api.domain.board.like.entity.LikeTipPosts;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslTestConfig.class)
public class LikeCommentRepositoryTest {
    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private EntityManager em;

    private Users testUser;
    private TipPosts testPost;
    private Comments testComment;

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

        testComment = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 댓글")
                .build();
        em.persist(testComment);
    }

    @Test
    @DisplayName("findByUserIdAndCommentIdIn: 추천 있음 - LikeComments 리스트 반환")
    void findByUserIdAndCommentIdIn_exist() {
        //given
        LikeComments likeComment = new LikeComments(testUser.getId(), testComment.getId());
        likeCommentRepository.save(likeComment);

        List<Long> commentIds = List.of(testComment.getId());

        //when
        List<LikeComments> result = likeCommentRepository.findByUserIdAndCommentIdIn(testUser.getId(), commentIds);

        //then
        assertEquals(1, result.size());
        assertEquals(testComment.getId(), result.get(0).getCommentId());
    }

    @Test
    @DisplayName("findByUserIdAndCommentIdIn: 추천 없음 - 빈 리스트 반환")
    void findByUserIdAndCommentIdIn_empty() {
        //given
        LikeComments likeComment = new LikeComments(testUser.getId(), testComment.getId());
        likeCommentRepository.save(likeComment);

        List<Long> commentIds = List.of(9999L);

        //when
        List<LikeComments> result = likeCommentRepository.findByUserIdAndCommentIdIn(testUser.getId(), commentIds);

        //then
        assertTrue(result.isEmpty());
    }
}
