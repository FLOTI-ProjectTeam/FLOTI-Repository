package com.floti.api.domain.board.discussion.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
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

    private DiscussionRooms testRoom;

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

        testRoom = DiscussionRooms.builder()
                .author(user1)
                .title("테스트 제목")
                .content("테스트 내용")
                .maxParticipantCount(4)
                .build();
        em.persist(testRoom);

        DiscussionParticipants dp1 = new DiscussionParticipants(testRoom, user2);
        DiscussionParticipants dp2 = new DiscussionParticipants(testRoom, user3);
        DiscussionParticipants dp3 = new DiscussionParticipants(testRoom, user4);
        discussionParticipantRepository.saveAll(List.of(dp1, dp2, dp3));

        for (int i=0; i<4; i++) testRoom.incrementParticipantCount();
    }

    @Test
    @DisplayName("findByRoomId: 참가자 있음 - DiscussionParticipants 리스트 반환")
    void findByRoomId_exist() {
        //when
        List<DiscussionParticipants> result = discussionParticipantRepository.findByRoomId(testRoom.getId());

        //then
        assertEquals(3, result.size());
        assertEquals("테스터02", result.get(0).getParticipant().getNickname());
    }

    @Test
    @DisplayName("findByRoomId: 참가자 없음 - 빈 리스트 반환")
    void findByRoomId_empty() {
        //when
        List<DiscussionParticipants> result = discussionParticipantRepository.findByRoomId(9999L);

        //then
        assertTrue(result.isEmpty());
    }
}
