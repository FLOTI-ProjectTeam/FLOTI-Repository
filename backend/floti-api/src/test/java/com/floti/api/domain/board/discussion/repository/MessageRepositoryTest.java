package com.floti.api.domain.board.discussion.repository;

import com.floti.api.config.QuerydslConfig;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.discussion.entity.DiscussionRooms;
import com.floti.api.domain.board.discussion.entity.Messages;
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
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager em;

    private DiscussionRooms testRoom;
    private Messages testMessage;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        em.persist(testUser);

        testRoom = DiscussionRooms.builder()
                .author(testUser)
                .title("테스트 제목")
                .intro("테스트 내용")
                .maxParticipants(2)
                .build();
        em.persist(testRoom);

        testMessage = Messages.builder()
                .roomId(testRoom.getId())
                .author(testUser)
                .content("첫번째 메시지")
                .build();
        Messages message1 = Messages.builder()
                .roomId(testRoom.getId())
                .author(testUser)
                .content("두번째 메시지")
                .build();
        Messages message2 = Messages.builder()
                .roomId(testRoom.getId())
                .author(testUser)
                .content("세번째 메시지")
                .build();
        Messages message3 = Messages.builder()
                .roomId(testRoom.getId())
                .author(testUser)
                .content("네번째 메시지")
                .build();
        messageRepository.saveAll(List.of(testMessage, message1, message2, message3));
    }

    @Test
    @DisplayName("findTop50ByRoomIdOrderByIdDesc: 메시지 있음 - Messages 리스트 반환")
    void findTop50ByRoomIdOrderByIdDesc_exist() {
        //when
        List<Messages> result = messageRepository.findTop50ByRoomIdOrderByIdDesc(testRoom.getId());

        //then
        assertEquals(4, result.size());
        assertEquals("네번째 메시지", result.get(0).getContent());
    }

    @Test
    @DisplayName("findTop50ByRoomIdOrderByIdDesc: 메시지 없음 - 빈 리스트 반환")
    void findTop50ByRoomIdOrderByIdDesc_empty() {
        //when
        List<Messages> result = messageRepository.findTop50ByRoomIdOrderByIdDesc(9999L);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findTop50ByRoomIdAndIdLessThanOrderByIdDesc: 메시지 있음 - 기준 ID 이전의 Messages 리스트 반환")
    void findTop50ByRoomIdAndIdLessThanOrderByIdDesc_exist() {
        //when
        List<Messages> result = messageRepository.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(
                testRoom.getId(), testMessage.getId() + 1
        );

        //then
        assertEquals(1, result.size());
        assertEquals("첫번째 메시지", result.get(0).getContent());
    }

    @Test
    @DisplayName("findTop50ByRoomIdAndIdLessThanOrderByIdDesc: 메시지 없음 - 빈 리스트 반환")
    void findTop50ByRoomIdAndIdLessThanOrderByIdDesc_empty() {
        //when
        List<Messages> result = messageRepository.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(
                testRoom.getId(), testMessage.getId()
        );

        //then
        assertTrue(result.isEmpty());
    }
}
