package com.floti.api.auth.service;

import com.floti.api.auth.entity.User;
import com.floti.api.auth.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Repository 계층만 로드
@ActiveProfiles("test") // application-test.yml 사용
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 테스트 순서 지정 가능
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        // 모든 유저 삭제
        userRepository.findAll().forEach(user -> {
            userRepository.delete(user);
        });
    }

    @Test
    @DisplayName("이메일로 유저 저장하고 조회하기")
    @Order(1)
    void findByEmail_success() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .username("tester")
                .password("pass1234")
                .nickname("테스터")
                .lastLogin(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("tester");
    }

    @Test
    @DisplayName("중복된 username이 있는지 확인")
    @Order(2)
    void existsByUsername() {
        // given
        User user = User.builder()
                .email("abc@example.com")
                .username("duplicateUser")
                .password("pass456")
                .nickname("복붙이")
                .lastLogin(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUsername("duplicateUser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("없는 이메일은 조회 결과가 없어야 함")
    @Order(3)
    void findByEmail_fail() {
        // when
        Optional<User> result = userRepository.findByEmail("notfound@email.com");

        // then
        assertThat(result).isEmpty();
    }
}