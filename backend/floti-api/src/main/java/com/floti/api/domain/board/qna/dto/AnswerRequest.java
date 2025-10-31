package com.floti.api.domain.board.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //테스트용
public class AnswerRequest {
    @NotBlank
    @Size(max = 1000)
    private String content;
}
