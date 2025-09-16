package com.floti.api.domain.board.tip.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslConfig.class)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private TipPosts testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
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
    @DisplayName("findByPostId: 댓글 있음 - Comments 리스트 반환")
    void findByPostId_exist() {
        //given
        Comments comment1 = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 댓글")
                .build();
        Comments comment2 = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("두번째 댓글")
                .build();
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        List<Comments> result = commentRepository.findByPostId(testPost.getId());

        //then
        assertEquals(2, result.size());
        assertEquals("첫번째 댓글", result.get(0).getContent());
    }

    @Test
    @DisplayName("findByPostId: 댓글 없음 - 빈 리스트 반환")
    void findByPostId_empty() {
        //given
        Comments comment = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 댓글")
                .build();
        commentRepository.save(comment);

        //when
        List<Comments> result = commentRepository.findByPostId(9999L);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByIdAndPostId: 게시글 댓글 맞음 - Comments 반환")
    void findByIdAndPostId_exist() {
        //given
        Comments comment = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 댓글")
                .build();
        commentRepository.save(comment);

        //when
        Optional<Comments> result = commentRepository.findByIdAndPostId(comment.getId(), testPost.getId());

        //then
        assertTrue(result.isPresent());
        assertEquals("첫번째 댓글", result.get().getContent());
    }

    @Test
    @DisplayName("findByIdAndPostId: 게시글 댓글 아님 - 빈 옵셔널 반환")
    void findByIdAndPostId_empty() {
        //given
        Comments comment = Comments.builder()
                .postId(testPost.getId())
                .author(testUser)
                .content("첫번째 댓글")
                .build();
        commentRepository.save(comment);

        //when
        Optional<Comments> result = commentRepository.findByIdAndPostId(comment.getId(), 9999L);

        //then
        assertTrue(result.isEmpty());
    }
}
