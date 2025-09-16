package com.floti.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 코드 검증 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeVerifyRequestDto {
    @NotBlank(message = "코드는 필수입니다.")
    private String code;              // 받은 인증코드
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;             // 어떤 이메일에 대한 코드인지
}
