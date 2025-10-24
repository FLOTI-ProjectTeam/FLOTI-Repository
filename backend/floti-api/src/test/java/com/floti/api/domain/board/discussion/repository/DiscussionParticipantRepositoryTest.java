package com.floti.api.domain.board.discussion.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
@Import(QuerydslConfig.class)
public class DiscussionParticipantRepositoryTest {
    @Autowired
    private DiscussionParticipantRepository discussionParticipantRepository;

    @Autowired
    private EntityManager em;

    private DiscussionPosts testPost;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        User user2 = User.builder()
                .email("test02@gmail.com")
                .username("test02")
                .password("password123")
                .nickname("테스터02")
                .build();
        User user3 = User.builder()
                .email("test03@gmail.com")
                .username("test03")
                .password("password123")
                .nickname("테스터03")
                .build();
        User user4 = User.builder()
                .email("test04@gmail.com")
                .username("test04")
                .password("password123")
                .nickname("테스터04")
                .build();
        List<User> users = List.of(user1, user2, user3, user4);
        users.forEach(em::persist);

        testPost = DiscussionPosts.builder()
                .author(user1)
                .title("테스트 제목")
                .intro("테스트 내용")
                .maxParticipants(4)
                .build();
        em.persist(testPost);

        DiscussionParticipants dp1 = new DiscussionParticipants(testPost, user2);
        DiscussionParticipants dp2 = new DiscussionParticipants(testPost, user3);
        DiscussionParticipants dp3 = new DiscussionParticipants(testPost, user4);
        discussionParticipantRepository.saveAll(List.of(dp1, dp2, dp3));

        for (int i=0; i<4; i++) testPost.incrementParticipantCount();
    }

    @Test
    @DisplayName("findByPostId: 참가자 있음 - DiscussionParticipants 리스트 반환")
    void findByPostId_exist() {
        //when
        List<DiscussionParticipants> result = discussionParticipantRepository.findByPostId(testPost.getId());

        //then
        assertEquals(3, result.size());
        assertEquals("테스터02", result.get(0).getParticipant().getNickname());
    }

    @Test
    @DisplayName("findByPostId: 참가자 없음 - 빈 리스트 반환")
    void findByPostId_empty() {
        //when
        List<DiscussionParticipants> result = discussionParticipantRepository.findByPostId(9999L);

        //then
        assertTrue(result.isEmpty());
    }
}
