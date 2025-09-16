package com.floti.api.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 응답 바디
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String jwt;               // 액세스 토큰
    private String nickname;          // 닉네임(응답 명세대로)
    private String refreshToken; // [추가됨] Refresh 토큰
}