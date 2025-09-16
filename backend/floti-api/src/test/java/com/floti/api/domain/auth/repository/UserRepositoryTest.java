package com.floti.api.domain.auth.repository;

import com.floti.api.domain.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test") // src/test/resources/application-test.yml 사용
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository; // 테스트 대상

    @Autowired
    private TestEntityManager em; // persist(), flush() 등을 테스트 친화적으로 제공

    // ---------- 헬퍼 메서드: 테스트 픽스처(샘플 유저) 생성 ----------
    private User newUser(String email, String username, String nickname) {
        return User.builder()
                .email(email)
                .username(username)
                .password("encodePW")
                .nickname(nickname)
                .build();
    }

    @Test
    @DisplayName("findByEmail: 저장한 유저를 이메일로 정확히 조회할 수 있다")
    void findByEmail_success() {
        // given: 유저 하나를 저장하고 즉시 DB에 반영(flush)해서 제약조건도 바로 체크
        User saved = em.persistAndFlush(newUser("a@a.com", "user1", "닉1"));

        // when: 이메일로 조회
        Optional<User> found = userRepository.findByEmail("a@a.com");

        // then: 존재해야 하고, username이 기대값과 동일해야 한다
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("existsByEmail: 해당 이메일이 존재하면 true를 반환한다")
    void existsByEmail_true() {
        // given
        em.persistAndFlush(newUser("b@a.com", "user2", "닉2"));

        // when
        boolean exists = userRepository.existsByEmail("b@a.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername: 해당 username이 존재하면 true를 반환한다")
    void existsByUsername_true() {
        // given
        em.persistAndFlush(newUser("c@a.com", "user3", "닉3"));

        // when
        boolean exists = userRepository.existsByUsername("user3");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername: 없는 username이면 false를 반환한다")
    void existsByUsername_false() {
        // when
        boolean exists = userRepository.existsByUsername("no-user");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("유니크 제약(email): 동일 이메일 두 번 저장 시 예외 (Spring 예외 번역)")
    void unique_email_violates_spring() {
        // given
        userRepository.saveAndFlush(newUser("dup@a.com", "u1", "닉"));

        // when & then
        assertThatThrownBy(() ->
                userRepository.saveAndFlush(newUser("dup@a.com", "u2", "닉"))
        )
                .as("email unique 제약 위반 시 Spring의 DataIntegrityViolationException 발생")
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }


}
