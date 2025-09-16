package com.floti.api.domain.auth.controller;

// ---- 스프링 MVC 테스트 관련 ----

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floti.api.domain.auth.dto.ChangePasswordRequestDto;
import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.security.jwt.JwtUtil;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class) // 이 테스트는 UserController만 로드
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("비밀번호 변경 성공(인증된 사용자) → 200")
    void changePassword_ok() throws Exception {
        // given: 현재 인증된 사용자명 = "user1"
        // DB에도 같은 username 유저가 있음
        User user = User.builder()
                .email("a@a.com")
                .username("user1")
                .password("ENC(old)")
                .build();
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));
        given(passwordEncoder.encode("newPW123!")).willReturn("ENC(new)");

        ChangePasswordRequestDto req = new ChangePasswordRequestDto("newPW123!");

        mockMvc.perform(patch("/user/change-password")
                        .with(user("user1").roles("USER")) // 가짜 인증 주입
                        .with(csrf())                        // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원탈퇴 성공(인증된 사용자) → 200")
    void withdraw_ok() throws Exception {
        User user = User.builder()
                .email("a@a.com").username("user1").password("ENC").build();
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        mockMvc.perform(delete("/user/withdraw")
                        .with(user("user1"))
                        .with(csrf()))   // ✅ CSRF 토큰 같이 보냄
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userRepository, times(1)).delete(user);
    }
}
