package com.floti.api.domain.board.tip.repository;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.tip.entity.TipPosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test") //application-test.yml 사용
public class TipPostRepositoryTest {
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
    @DisplayName("findByTitleContainingIgnoreCase: 검색어 있음 - 게시글 반환")
    void findByTitleContainingIgnoreCase_exist() {
        //given
        TipPosts searchPost1 = TipPosts.builder()
                .author(testUser)
                .title("검색할 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(searchPost1);

        TipPosts searchPost2 = TipPosts.builder()
                .author(testUser)
                .title("검색할 제목")
                .content("검색할 내용")
                .build();
        tipPostRepository.save(searchPost2);

        //when
        Page<TipPosts> result = tipPostRepository.findByTitleContainingIgnoreCase(
                PageRequest.of(0, 20), "검색"
        );

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("검색할 제목", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: 검색어 없음 - 빈 페이지 반환")
    void findByTitleContainingIgnoreCase_empty() {
        //when
        Page<TipPosts> result = tipPostRepository.findByTitleContainingIgnoreCase(
                PageRequest.of(0, 20), "검색"
        );

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: 대소문자 무시 - 게시글 반환")
    void findByTitleContainingIgnoreCase_ignoreCase() {
        //given
        TipPosts searchPost = TipPosts.builder()
                .author(testUser)
                .title("Test Title")
                .content("Test Content")
                .build();
        tipPostRepository.save(searchPost);

        //when
        Page<TipPosts> result = tipPostRepository.findByTitleContainingIgnoreCase(
                PageRequest.of(0, 20), "TEST"
        );

        //then
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
    }
}
