package com.floti.api.domain.board.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //테스트용
public class MessageRequest {
    @NotBlank
    @Size(max = 255)
    private String content;
}
