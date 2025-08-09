package com.floti.api.auth.service;

import com.floti.api.auth.entity.User;
import com.floti.api.auth.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UserRepository 동작을 검증하는 단위 테스트 클래스
 */
@DataJpaTest                        // JPA 관련 컴포넌트만 로드 (Service, Controller는 로드 안 함)
@ActiveProfiles("test")             // application-test.yml 환경 설정 사용 (H2 DB 등 테스트 전용 설정)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order 순서대로 테스트 실행
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository; // 태스트 대상 Repository

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // 매 테스트 시작 전 데이터 초기화
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll(); // 혹시 모를 잔여 데이터 제거
    }

    @Test
    @DisplayName("이메일로 유저 저장하고 조회하기")
    @Order(1)
    void findByEmail_success() {
        // given: 테스트에 사용할 User 객체 생성
        User user = User.builder()
                .email("test@example.com")
                .username("tester")
                .password("pass1234")
                .nickname("테스터")
                .lastLogin(LocalDateTime.now())
                .build();

        userRepository.saveAndFlush(user); // 즉시 DB 반영

        // when: 이메일로 유저 검색
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then: 유저가 존재하고, username이 예상 값과 일치하는지 검증
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("tester");
    }

    @Test
    @DisplayName("중복된 username이 있는지 확인 (existsByUsername)")
    @Order(2)
    void existsByUsername() {
        // given
        User user = User.builder()
                .email("abc@example.com")
                .username("dupUser")
                .password("pass456")
                .nickname("복붙이")
                .lastLogin(LocalDateTime.now())
                .build();

        userRepository.saveAndFlush(user);

        // when: 해당 username이 존재하는지 체크
        boolean exists = userRepository.existsByUsername("dupUser");

        // then: true가 나와야 함
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    // 기본적으로 @DataJpaTest는 트랜잭션 안에서 실행됨 → 롤백으로 예외가 안 보일 수 있음
    // propagation = NOT_SUPPORTED → 트랜잭션 끊고 실행 → 제약 조건 위반 즉시 발생
    @DisplayName("중복된 username 저장 시 예외 발생")
    @Order(3)
    void saveDuplicateUsername_shouldFail() {
        // given
        User user1 = User.builder()
                .email("a@example.com")
                .username("dupUser")
                .password("pass456")
                .nickname("복붙이1")
                .lastLogin(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .email("b@example.com")
                .username("dupUser") // username 중복
                .password("pass789")
                .nickname("복붙이2")
                .lastLogin(LocalDateTime.now())
                .build();

        userRepository.saveAndFlush(user1); // 첫 번째 유저 저장

        // then: 두 번째 유저 저장 시 UNIQUE 제약조건 위반 예외 발생해야 함
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("없는 이메일은 조회 결과가 없어야 함")
    @Order(4)
    void findByEmail_fail() {
        // when: 존재하지 않는 이메일로 조회
        Optional<User> result = userRepository.findByEmail("notfound@email.com");

        // then: 결과가 비어있어야 함
        assertThat(result).isEmpty();
    }
}
