package com.floti.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 이메일만 받는 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;             // 대상 이메일
}