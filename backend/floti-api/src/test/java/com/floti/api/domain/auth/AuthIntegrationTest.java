package com.floti.api.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floti.api.domain.auth.dto.LoginRequestDto;
import com.floti.api.domain.auth.dto.SignUpRequestDto;
import com.floti.api.security.jwt.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // 가짜 서블릿 컨테이너를 통한 HTTP 요청/응답 시뮬레이션

    @Autowired
    private ObjectMapper om; // JSON 직렬화/역직렬화 도구

    @Autowired
    private RefreshTokenService refreshTokenService; // Redis 저장 확인용

    @Test
    @DisplayName("회원가입 → 로그인 → Refresh 재발급(토큰값 변경) → 로그아웃 전체 흐름")
    void auth_flow_success() throws Exception {
        // 1) 회원가입 요청 DTO 만들기
        SignUpRequestDto signup = new SignUpRequestDto();
        signup.setEmail("test@a.com");
        signup.setUsername("user1");
        signup.setPassword("pw123456");
        signup.setNickname("닉네임");

        // /auth/signup POST 요청 → 회원가입 성공(201)
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        // 2) 로그인 요청 DTO
        LoginRequestDto login = new LoginRequestDto("user1", "pw123456");

        // /auth/login POST 요청 → Access + Refresh 반환
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON 파싱 → 토큰 추출
        String accessToken = om.readTree(loginResponse).path("data").path("jwt").asText();
        String refreshToken = om.readTree(loginResponse).path("data").path("refreshToken").asText();

        // Redis 저장 여부 확인
        String redisValue = refreshTokenService.get("user1");
        assertThat(redisValue).isEqualTo(refreshToken);

        // 3) Refresh로 Access 재발급 → 기존 accessToken과 달라야 함
        String refreshResponse = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newAccessToken = om.readTree(refreshResponse).path("accessToken").asText();
        assertThat(newAccessToken).isNotEqualTo(accessToken); // 새 토큰이 발급되었는지 확인

        // 4) 로그아웃 요청 → Redis에서 삭제 확인
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out"));

        String afterLogout = refreshTokenService.get("user1");
        assertThat(afterLogout).isNull(); // 로그아웃 후 저장소에서 제거됨
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_wrongPassword() throws Exception {
        // 사전 회원가입
        SignUpRequestDto signup = new SignUpRequestDto();
        signup.setEmail("wrongpw@a.com");
        signup.setUsername("wronguser");
        signup.setPassword("pw123456");
        signup.setNickname("닉네임");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(signup)));

        // 잘못된 비번으로 로그인 시도 → 401
        LoginRequestDto login = new LoginRequestDto("wronguser", "wrongPW");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Refresh 실패 - 잘못된 토큰")
    void refresh_fail_invalidToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"fake.token.value\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));
    }
}
