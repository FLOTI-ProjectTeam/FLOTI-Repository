package com.floti.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 아이디 중복 확인 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameCheckRequestDto {
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(max = 10, message = "아이디는 10자 이하만 가능합니다.")
    private String username;          // 확인할 아이디
}
