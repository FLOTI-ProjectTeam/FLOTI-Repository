package com.floti.api.domain.auth.controller;

// ---- 스프링 MVC 테스트 관련 임포트 ----

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floti.api.domain.auth.code.CodeType;
import com.floti.api.domain.auth.code.InMemoryCodeService;
import com.floti.api.domain.auth.dto.*;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.auth.service.UserService;
import com.floti.api.security.jwt.JwtUtil;
import com.floti.api.security.jwt.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class) // 이 테스트는 AuthController만 로드(웹 레이어만 가볍게)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화(공개 API 검증에만 집중)
@ActiveProfiles("test") // 테스트 프로필
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // 가짜 HTTP 요청/응답

    @Autowired
    private ObjectMapper om; // 객체 <-> JSON 변환

    // ----- 컨트롤러가 주입받는 의존성들을 MockBean으로 대체 -----
    @MockitoBean
    private UserService userService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private PasswordEncoder passwordEncoder;
    @MockitoBean private InMemoryCodeService codeService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private RefreshTokenService refreshTokenService; // [추가됨]

    // ---------- 회원가입 ----------
    @Test
    @DisplayName("회원가입 성공 -> 201")
    void signup_success() throws Exception {
        // given: 서비스 호출 시 아무 예외도 안 던진다고 가정
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("a@a.com");
        dto.setUsername("user1");
        dto.setPassword("pw123456");
        dto.setNickname("닉");

        // when & then
        mockMvc.perform(post("/auth/signup")                               // POST /auth/signup
                        .contentType(MediaType.APPLICATION_JSON)                    // JSON 요청
                        .content(om.writeValueAsString(dto)))                       // DTO -> JSON
                        .andExpect(status().isCreated())                            // 201
                        .andExpect(jsonPath("$.success").value(true))               // 성공 플래그
                        .andExpect(jsonPath("$.message").value("회원가입 성공"));     // 메시지
        verify(userService, times(1)).signup(any(SignUpRequestDto.class));  // 서비스가 1번 호출
    }

    @Test
    @DisplayName("회원가입 실패(이메일 중복) → 409")
    void signup_conflict_email() throws Exception {
        // given: 서비스가 이메일 중복으로 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                .when(userService).signup(any(SignUpRequestDto.class));

        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("dup@a.com");
        dto.setUsername("user1");
        dto.setPassword("pw123456");
        dto.setNickname("닉");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                        .andExpect(status().isConflict())                            // 409
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));

    }

    // ---------- 로그인 ----------
    @Test
    @DisplayName("로그인 성공 → 200 + access, refresh, nickname")
    void login_success() throws Exception {
        // given: DB에 사용자 존재 + 비번 일치 + 토큰 발급
        User user = User.builder()
                .email("a@a.com")
                .username("user1")
                .password("ENC(pw)")     // DB에 저장된 해시(예시)
                .nickname("닉")
                .build();

        // 레포지토리에서 username으로 찾게 설정(권장 메서드)
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("pw123456", "ENC(pw)")).willReturn(true);
        given(jwtUtil.generateToken("user1")).willReturn("ACCESS_TOKEN"); // [변경됨]
        given(jwtUtil.generateRefreshToken("user1")).willReturn("REFRESH_TOKEN"); // [추가됨]

        LoginRequestDto req = new LoginRequestDto("user1", "pw123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").value("ACCESS_TOKEN"))
                .andExpect(jsonPath("$.data.refreshToken").value("REFRESH_TOKEN")) // [추가됨]
                .andExpect(jsonPath("$.data.nickname").value("닉"));
    }

    @Test
    @DisplayName("로그인 실패(비번 불일치) → 401")
    void login_wrong_password() throws Exception {
        User user = User.builder()
                .email("a@a.com")
                .username("user1")
                .password("ENC(pw)") // 저장된 해시
                .nickname("닉")
                .build();

        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("badpw", "ENC(pw)")).willReturn(false);

        LoginRequestDto req = new LoginRequestDto("user1", "badpw");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ---------- Refresh ----------
    @Test
    @DisplayName("Refresh 성공 → 200 + 새 access")
    void refresh_success() throws Exception {
        String refreshToken = "REFRESH_TOKEN";

        given(jwtUtil.isValid(refreshToken)).willReturn(true);
        given(jwtUtil.extractUsername(refreshToken)).willReturn("user1");
        given(refreshTokenService.get("user1")).willReturn(refreshToken);
        given(jwtUtil.generateToken("user1")).willReturn("NEW_ACCESS");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"REFRESH_TOKEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("NEW_ACCESS"));
    }

    @Test
    @DisplayName("Refresh 실패(잘못된 토큰) → 401")
    void refresh_fail() throws Exception {
        String refreshToken = "BAD_TOKEN";

        given(jwtUtil.isValid(refreshToken)).willReturn(false);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"BAD_TOKEN\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));
    }

    // ---------- 로그아웃 ----------
    @Test
    @DisplayName("로그아웃 성공 → 200")
    void logout_success() throws Exception {
        String refreshToken = "REFRESH_TOKEN";

        given(jwtUtil.isValid(refreshToken)).willReturn(true);
        given(jwtUtil.extractUsername(refreshToken)).willReturn("user1");

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"REFRESH_TOKEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out"));

        verify(refreshTokenService, times(1)).delete("user1");
    }

    // ---------- 아이디 찾기 ----------
    @Test
    @DisplayName("아이디 찾기 성공 → 200 username 반환")
    void findUsername_ok() throws Exception {
        User user = User.builder().email("a@a.com").username("user1").build();
        given(userRepository.findByEmail("a@a.com")).willReturn(Optional.of(user));

        EmailRequestDto req = new EmailRequestDto("a@a.com");

        mockMvc.perform(post("/auth/find-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("user1"));
    }

    @Test
    @DisplayName("아이디 찾기 실패(없음) → 404")
    void findUsername_notFound() throws Exception {
        given(userRepository.findByEmail("x@x.com")).willReturn(Optional.empty());

        EmailRequestDto req = new EmailRequestDto("x@x.com");

        mockMvc.perform(post("/auth/find-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ---------- 회원가입 코드 전송/검증 ----------
    @Test
    @DisplayName("회원가입 코드 전송 → 200")
    void sendSignupCode_ok() throws Exception {
        given(codeService.issue(CodeType.SIGNUP, "a@a.com")).willReturn("NP1c004S");

        EmailRequestDto req = new EmailRequestDto("a@a.com");

        mockMvc.perform(post("/auth/signup/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원가입 코드 검증 성공 → 200")
    void verifySignupCode_ok() throws Exception {
        given(codeService.verify(CodeType.SIGNUP, "a@a.com", "CODE1234")).willReturn(true);

        CodeVerifyRequestDto req = new CodeVerifyRequestDto("CODE1234", "a@a.com");

        mockMvc.perform(post("/auth/signup/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(codeService, times(1)).consume(CodeType.SIGNUP, "a@a.com");
    }

    @Test
    @DisplayName("회원가입 코드 검증 실패 → 400")
    void verifySignupCode_bad() throws Exception {
        given(codeService.verify(CodeType.SIGNUP, "a@a.com", "BAD")).willReturn(false);

        CodeVerifyRequestDto req = new CodeVerifyRequestDto("BAD", "a@a.com");

        mockMvc.perform(post("/auth/signup/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ---------- 비밀번호 재설정 ----------
    @Test
    @DisplayName("비밀번호 재설정 성공 → 200")
    void resetPassword_ok() throws Exception {
        User user = User.builder().email("a@a.com").username("user1").password("ENC(old)").build();
        given(userRepository.findByEmail("a@a.com")).willReturn(Optional.of(user));
        given(passwordEncoder.encode("newPW123!")).willReturn("ENC(new)");

        ResetPasswordRequestDto req = new ResetPasswordRequestDto("a@a.com", "newPW123!");

        mockMvc.perform(patch("/auth/find-password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userRepository, times(1)).save(any(User.class)); // 저장 호출 확인
    }

}
