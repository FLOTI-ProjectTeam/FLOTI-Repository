package com.floti.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(max = 10, message = "사용자 이름은 10자 이하만 가능합니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 15, message = "닉네임은 15자 이하만 가능합니다.")
    private String nickname;
}
