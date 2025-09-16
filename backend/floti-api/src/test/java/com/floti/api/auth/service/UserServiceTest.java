package com.floti.api.auth.service;

import com.floti.api.auth.dto.SignUpRequestDto;
import com.floti.api.auth.entity.User;
import com.floti.api.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //JUnit5에 Mockito 확장 등록(목 객체 자동 초기화)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("signup 성공: 중복 아님 → 비번 인코딩 후 저장")
    void signup_success() {
        // given: 컨트롤러가 넘겨줄 DTO 흉내
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("ok@a.com");
        dto.setUsername("user1");
        dto.setPassword("rawPW");
        dto.setNickname("닉1");

        // 스텁(가짜 동작): 중복 체크 결과는 모두 false, 인코딩 결과는 "ENC(rawPW)"
        when(userRepository.existsByEmail("ok@a.com")).thenReturn(false);
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(passwordEncoder.encode("rawPW")).thenReturn("ENC(rawPW)");

        //when: 서비스 호출
        userService.signup(dto);

        // then: 레포 save로 실제로 어떤 User가 넘어갔는지 캡쳐해서 필드 검증
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture()); // 한 번 저장되어야 함
        User saved = captor.getValue();

        // 저장 필드 값 검증
        assertThat(saved.getEmail()).isEqualTo("ok@a.com");
        assertThat(saved.getUsername()).isEqualTo("user1");
        assertThat(saved.getPassword()).isEqualTo("ENC(rawPW)"); // 반드시 인코딩된 값이어야 함
        assertThat(saved.getNickname()).isEqualTo("닉1");
        assertThat(saved.getProfileImage()).isNull(); // 서비스 코드가 null로 넣음
        assertThat(saved.getLastLogin()).isNotNull(); // now()로 들어가므로 null이면 안 됨

        // 호출 순서/횟수 같은 상호작용도 확인(의도한 비즈니스 흐름 보장)
        verify(userRepository).existsByEmail("ok@a.com");
        verify(userRepository).existsByUsername("user1");
        verify(passwordEncoder).encode("rawPW");
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("signup 실패: 이메일 중복이면 즉시 예외, 인코딩/저장은 호출 X")
    void signup_fail_duplicateEmail() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("dup@a.com");
        dto.setUsername("user1");
        dto.setPassword("rawPW");
        dto.setNickname("닉1");

        // 이메일 중복이라고 응답
        when(userRepository.existsByEmail("dup@a.com")).thenReturn(true);

        // when & then: 예외 타입과 메시지 검증
        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");

        // 이메일에서 걸렸으므로 그 다음 로직은 아예 실행되면 안 됨
        verify(userRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());

    }

    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("signup 실패: username 중복이면 예외, 인코딩/저장은 호출 X")
    void signup_fail_duplicateUsername() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("ok@a.com");
        dto.setUsername("dupUser");
        dto.setPassword("rawPW");
        dto.setNickname("닉1");

        // 이메일은 통과(false), username에서 걸림(true)
        when(userRepository.existsByEmail("ok@a.com")).thenReturn(false);
        when(userRepository.existsByUsername("dupUser")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 사용자 아이디입니다.");

        // 이후 인코딩/저장 호출 금지
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

}
